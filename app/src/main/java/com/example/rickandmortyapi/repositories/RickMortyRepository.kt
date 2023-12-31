package com.example.rickandmortyapi.repositories

import androidx.paging.PagingData
import com.example.rickandmortyapi.model.Character
import com.example.rickandmortyapi.model.CharactersResponseDto
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.util.Resource
import kotlinx.coroutines.flow.Flow

interface RickMortyRepository {

    suspend fun insertCharacter(character: CharactersResponseEntity)

    suspend fun deleteCharacters()

    suspend fun getCharacterByName(characterName: String): Flow<PagingData<Character>>

    suspend fun getCharacters(imageQuery: String, page: Int): Resource<CharactersResponseDto>

    fun getCharacterById(id: Int): Resource<CharactersResponseEntity>
}