package com.example.rickandmortyapi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.repositories.RickMortyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.GetAllCharactersByName -> onSearch(event.characterName)
        }
    }

    private fun onSearch(query: String) {
        searchQuery.value = query
    }
}