package com.example.rickandmortyapi.repositories

import androidx.paging.PagingData
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRickMortyRepository : RickMortyRepository {

    private val rickMortyCharacters = mutableListOf<CharactersResponseEntity>()
    private var charactersFlow: Flow<PagingData<CharactersResponseEntity>> = flowOf()
    private var filteredData: MutableMap<String, List<CharactersResponseEntity>> = mutableMapOf()

    fun setReturnValue(returnValue: Flow<PagingData<CharactersResponseEntity>>) {
        charactersFlow = returnValue
    }

    fun setSearchResponse(query: String, filteredResults: List<CharactersResponseEntity>) {
        filteredData[query] = filteredResults
    }

    override suspend fun insertCharacter(character: CharactersResponseEntity) {
        rickMortyCharacters.add(character)
    }

    override suspend fun deleteCharacters() {
        rickMortyCharacters.clear()
    }

    override suspend fun getCharacterByName(characterName: String): Flow<PagingData<CharactersResponseEntity>> {
        return if (characterName.isEmpty()) {
            charactersFlow
        } else {
            flowOf(PagingData.from(filteredData[characterName] ?: emptyList()))
        }
    }

    override suspend fun getCharacterById(id: Int): Resource<CharactersResponseEntity> {
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

