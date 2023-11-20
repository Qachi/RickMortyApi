package com.example.rickandmortyapi.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.model.CharactersResponseDto
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.repositories.RickMortyRepository
import com.example.rickandmortyapi.util.Resource
import com.example.rickandmortyapi.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModel @Inject constructor(
    private val repository: RickMortyRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val charactersFlow = searchQuery.flatMapLatest {
        repository.getCharacterByName(it)
    }

    private val _updatedResource = MutableLiveData<Resource<CharactersResponseDto>>()
    val updatedResource: LiveData<Resource<CharactersResponseDto>> = _updatedResource

    private var searchJob: Job? = null

    fun insertCharacterIntoDB(character: CharactersResponseEntity) = viewModelScope.launch {
        repository.insertCharacter(character)
    }

    fun getCharacters(imageQuery: String, page: Int) {
        searchQuery.value = imageQuery
        viewModelScope.launch {
            val resource = repository.getCharacters(imageQuery, page)

            when (resource.status) {
                Status.SUCCESS -> {
                    val data = resource.data
                    Resource.success(data)
                }
                Status.ERROR -> {
                    val message = resource.message
                    if (message != null) {
                        Resource.error(message, null)
                    } else {
                        Resource.error("An unknown error occurred", null)
                    }
                }
                Status.LOADING -> {
                    val data = resource.data
                    Resource.loading(data)
                }
            }
        }
    }

    fun deleteCharacters() = viewModelScope.launch {
        repository.deleteCharacters()
    }

    fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.GetAllCharactersByName -> onSearch(event.characterName)
        }
    }

     fun getCharactersByName(character: String) {
        viewModelScope.launch {
            repository.getCharacterByName(character)
        }
    }

    private fun onSearch(query: String) {
        searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getCharactersByName(query)
        }
    }
}