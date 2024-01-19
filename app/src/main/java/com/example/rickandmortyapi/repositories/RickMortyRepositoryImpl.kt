package com.example.rickandmortyapi.repositories

import androidx.paging.*
import com.example.rickandmortyapi.api.RickMortyApi
import com.example.rickandmortyapi.dao.RickMortyDao
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.remote.RickyMortyRemoteMediator
import com.example.rickandmortyapi.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RickMortyRepositoryImpl @Inject constructor(
    private val rickMortyDao: RickMortyDao,
    private val rickMortyApi: RickMortyApi,
    private val rickMortyDatabase: RickMortyDatabase,
) : RickMortyRepository {

    override suspend fun getCharacterByName(characterName: String): Flow<PagingData<CharactersResponseEntity>> {
        val rickMortyPagingSource =
            { rickMortyDatabase.getRickMortyDao().getCharactersByName(characterName) }
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
                jumpThreshold = Int.MIN_VALUE,
                enablePlaceholders = true
            ),
            remoteMediator = RickyMortyRemoteMediator(
                rickMortyApi,
                rickMortyDatabase,
                1,
                characterName
            ),
            pagingSourceFactory = rickMortyPagingSource
        ).flow
            //            .flowOn(dispatcher)
            .map { characterEntityPagingData ->
            characterEntityPagingData.map { characterEntity ->
                CharactersResponseEntity(
                    id = characterEntity.id,
                    name = characterEntity.name,
                    species = characterEntity.species,
                    gender = characterEntity.gender,
                    origin = characterEntity.origin,
                    location = characterEntity.location,
                    image = characterEntity.image,
                    created = characterEntity.created
                )
            }
        }
    }

    override suspend fun getCharacterById(id: Int): Resource<CharactersResponseEntity> {
        return withContext(Dispatchers.IO) {
            try {
                // First, try to fetch the character from the database
                val characterFromDb = rickMortyDatabase.getRickMortyDao().getCharacterById(id)
                if (characterFromDb != null) {
                    // If found in the database, return it
                    Resource.success(characterFromDb)
                } else {
                    // If not found in the database, try the network call
                    val response = rickMortyApi.getCharacterById(id)
                    if (response.isSuccessful) {
                        // If network call is successful, save to database and return
                        val characterFromNetwork = response.body()!!
                        rickMortyDatabase.getRickMortyDao().insertCharacter(characterFromNetwork)
                        Resource.success(characterFromNetwork)
                    } else {
                        // If network call fails, return error
                        Resource.error("Error fetching character with ID: $id", null)
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Resource.error("Exception occurred: ${e.message}", null)
            }
        }
    }

    override suspend fun insertCharacter(character: CharactersResponseEntity) {
        rickMortyDao.insertCharacter(character)
    }

    override suspend fun deleteCharacters() {
        rickMortyDao.deleteCharacters()
    }
}

