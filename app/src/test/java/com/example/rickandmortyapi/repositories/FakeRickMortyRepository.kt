package com.example.rickandmortyapi.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.example.rickandmortyapi.model.Character
import com.example.rickandmortyapi.model.CharactersResponseDto
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.model.InfoDto
import com.example.rickandmortyapi.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FakeRickMortyRepository : RickMortyRepository {

    private val rickMortyCharacters = mutableListOf<CharactersResponseEntity>()
    private var shouldReturnNetwork = false

    fun setShouldReturnNetwork(value: Boolean) {
        shouldReturnNetwork = value
    }

    override suspend fun insertCharacter(character: CharactersResponseEntity) {
        rickMortyCharacters.add(character)
    }

    override suspend fun deleteCharacters() {
        rickMortyCharacters.clear()
    }

    override suspend fun getCharacterByName(characterName: String): Flow<PagingData<Character>> {
        val pagingSource = object : PagingSource<Int, CharactersResponseEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharactersResponseEntity> {
                try {
                    // Filter characters by name from the list
                    val characters = rickMortyCharacters.filter { it.name == characterName }

                    // You may need to implement paging logic based on params (e.g., page size)
                    val prevKey = null
                    val nextKey = null

                    return LoadResult.Page(
                        data = characters,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, CharactersResponseEntity>): Int? {
                // You may implement this if necessary
                return null
            }
        }

        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { pagingSource }
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
        return if (shouldReturnNetwork) {
            Resource.error("Error", null)
        } else {
            val characters = rickMortyCharacters.filter { it.name == imageQuery }
            return if (characters.isEmpty()) {
                Resource.error("No characters found", null)
            } else {
                Resource.success(
                    CharactersResponseDto(
                        info = InfoDto(
                            0,
                            0,
                            null,
                            null
                        ),
                        listOf()
                    )
                )
            }
        }
    }

    override fun getCharacterById(id: Int): Resource<CharactersResponseEntity> {
        val fakeCharacter = CharactersResponseEntity(
            id = 1,
            name = "Rick",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "rick.png",
            created = "2023-10-18T12:34:56.789Z"
        )
        return Resource.success(fakeCharacter)
    }
}