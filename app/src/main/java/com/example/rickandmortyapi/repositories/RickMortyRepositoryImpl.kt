package com.example.rickandmortyapi.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmortyapi.api.RickMortyApi
import com.example.rickandmortyapi.dao.RickMortyDao
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.model.Character
import com.example.rickandmortyapi.model.CharactersResponseDto
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.remote.RickyMortyRemoteMediator
import com.example.rickandmortyapi.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RickMortyRepositoryImpl @Inject constructor(
    private val rickMortyDao: RickMortyDao,
    private val rickMortyApi: RickMortyApi,
    private val rickMortyDatabase: RickMortyDatabase
) : RickMortyRepository {

    override suspend fun getCharacterByName(characterName: String): Flow<PagingData<Character>> {
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
        ).flow.map { characterEntityPagingData ->
            characterEntityPagingData.map { characterEntity ->
                characterEntity.toCharacter()
            }
        }
    }

    override suspend fun getCharacters(
        imageQuery: String,
        page: Int
    ): Resource<CharactersResponseDto> {
        return try {
            val response = rickMortyApi.getCharacters(imageQuery, page)
            if (response.isSuccessful) {
                response.body().let {
                    return@let Resource.success(it)
                }
            } else {
                Resource.error("An unknown error occurred", null)
            }

        } catch (e: Exception) {
            Resource.error("Couldn't reach the server. Check your internet connection", null)
        }
    }

    override fun getCharacterById(id: Int): Resource<CharactersResponseEntity> {
        val character = rickMortyDao.getCharacterById(id)
        return if (character != null) {
            Resource.success(character)
        } else {
            Resource.error("Character not found", null)
        }
    }

    override suspend fun insertCharacter(character: CharactersResponseEntity) {
        rickMortyDao.insertCharacter(character)
    }

    override suspend fun deleteCharacters() {
        rickMortyDao.deleteCharacters()
    }

    fun getCharacter(id: Int): Flow<CharactersResponseEntity> {
        return flow {
            val rickMortyPagingSource = rickMortyDatabase.getRickMortyDao().getCharacter(id)
            if (rickMortyPagingSource != null) {
                emit(rickMortyPagingSource)

            } else {
                val character = rickMortyApi.getCharacterById(id).body()
                emit(character!!)
                rickMortyDatabase.getRickMortyDao().insertCharacter(character)
            }
        }.flowOn(Dispatchers.IO)
    }
}

