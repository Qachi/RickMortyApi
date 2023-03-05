package com.example.rickandmortyapi.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.rickandmortyapi.api.RickMortyApi
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.model.CharactersResponseRemoteKey
import com.example.rickandmortyapi.model.CharactersResponseEntity
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class RickyMortyRemoteMediator(
    private val rickMortyApi: RickMortyApi,
    private val rickyMortyDatabase: RickMortyDatabase,
    private val initialPage: Int = 1
) : RemoteMediator<Int, CharactersResponseEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharactersResponseEntity>
    ): MediatorResult {


        return try {

            val page = when (loadType) {
                LoadType.APPEND -> {
                    val remoteKey = getLastRemoteKey(state)
                        ?: throw InvalidObjectException("InvalidObjectException")
                    remoteKey.next ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.PREPEND -> {
                    val remoteKey = getFirstRemoteKey(state)
                        ?: throw InvalidObjectException("InvalidObjectException")
                    remoteKey.prev ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    val remoteKey = getClosestRemoteKey(state)
                    remoteKey?.next?.minus(1) ?: initialPage
                }

            }
            val response = rickMortyApi.getCharacters(page)
            val endOfPagination = response.body()?.results?.size!! < state.config.pageSize

            if (response.isSuccessful) {
                response.body().let {
                    if (loadType == LoadType.REFRESH) {

                        rickyMortyDatabase.getRickMortyDao().deleteCharacters()
                        rickyMortyDatabase.getRickyMortyRemoteKeyDao().deleteCharacter()
                    }
                    val prev = if (page == initialPage) null else page.minus(1)
                    val next = if (endOfPagination) null else page.plus(1)

                    val list = response.body()?.results?.map {
                        CharactersResponseRemoteKey(it.id, prev, next)

                    }
                    if (list != null) {
                        rickyMortyDatabase.getRickMortyDao()
                            .insertCharacters(response.body()?.results?.map {
                                it.toCharactersResponseEntity()
                            }!!)
                    }
                    if (list != null) {
                        rickyMortyDatabase.getRickyMortyRemoteKeyDao()
                            .insertCharactersRemoteKey(list)
                    }
                }
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }


    private suspend fun getClosestRemoteKey(state: PagingState<Int, CharactersResponseEntity>): CharactersResponseRemoteKey? {
        return state.anchorPosition?.let { it ->
            state.closestItemToPosition(it)?.let {
                rickyMortyDatabase.getRickyMortyRemoteKeyDao().getCharactersRemoteKey(it.id)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, CharactersResponseEntity>): CharactersResponseRemoteKey? {
        return state.lastItemOrNull()?.let {
            rickyMortyDatabase.getRickyMortyRemoteKeyDao().getCharactersRemoteKey(it.id)
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, CharactersResponseEntity>): CharactersResponseRemoteKey? {
        return state.firstItemOrNull()?.let {
            rickyMortyDatabase.getRickyMortyRemoteKeyDao().getCharactersRemoteKey(it.id)
        }
    }
}

