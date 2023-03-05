package com.example.rickandmortyapi.viewmodel

import androidx.lifecycle.*
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.repository.RickMortyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: RickMortyRepository
) : ViewModel() {


    private val searchQuery = MutableStateFlow("")
    val charactersFlow = searchQuery.flatMapLatest {
        repository.getCharacterByName(it)
    }


    private var searchJob: Job? = null

    fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.GetAllCharactersByName -> onSearch(event.characterName)
        }
    }


    private fun getCharactersByName(character: String) {
        repository.getCharacterByName(character)
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