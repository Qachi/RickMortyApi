package com.example.rickandmortyapi.repository

import androidx.paging.*
import com.example.rickandmortyapi.api.RickMortyApi
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.model.Character
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.remote.RickyMortyRemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RickMortyRepositoryImpl @Inject constructor(
    private val rickMortyApi: RickMortyApi,
    private val rickMortyDatabase: RickMortyDatabase
) : RickMortyRepository {

    override fun getCharacterByName(characterName: String): Flow<PagingData<Character>> {
        val rickMortyPagingSource = { rickMortyDatabase.getRickMortyDao().getCharactersByName(characterName) }

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
                jumpThreshold = Int.MIN_VALUE,
                enablePlaceholders = true
            ),
            remoteMediator = RickyMortyRemoteMediator(rickMortyApi, rickMortyDatabase,1),
            pagingSourceFactory = rickMortyPagingSource
        ).flow.map { CharacterEntityPagingData ->
            CharacterEntityPagingData.map { characterEntity ->
                characterEntity.toCharacter()
            }
        }
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

