package com.example.rickandmortyapi.event

sealed interface CharacterListEvent {
    data class GetAllCharactersByName(val characterName: String) : CharacterListEvent
}
