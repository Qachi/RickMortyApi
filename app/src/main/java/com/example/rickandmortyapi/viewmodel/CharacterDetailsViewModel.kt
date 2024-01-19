package com.example.rickandmortyapi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.repositories.RickMortyRepositoryImpl
import com.example.rickandmortyapi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val rickMortyRepository: RickMortyRepositoryImpl
) : ViewModel() {

    fun getCharacterById(id: Int): Flow<Resource<CharactersResponseEntity>> {
        return flow {
            try {
                emit(rickMortyRepository.getCharacterById(id))
            } catch (e: Exception) {
                emit(Resource.error("Exception occurred: ${e.message}", null))
            }
        }.flowOn(Dispatchers.IO)
    }
}