package com.example.rickandmortyapi.event

sealed class CharacterListEvent{
    data class GetAllCharactersByName(val characterName: String):CharacterListEvent()
}
