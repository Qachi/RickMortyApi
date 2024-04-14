package com.example.rickandmortyapi.data

import com.example.rickandmortyapi.model.CharactersResponseEntity

val listOfCharacters = mutableListOf(
    CharactersResponseEntity(
        id = 1,
        name = "Rick",
        species = "Human",
        gender = "Male",
        origin = "Earth",
        location = "Earth",
        image = "rick.png",
        created = "2023-10-18T12:34:56.789Z"
    ), CharactersResponseEntity(
        id = 2,
        name = "Morty",
        species = "Human",
        gender = "Male",
        origin = "Earth",
        location = "Earth",
        image = "morty.png",
        created = "2023-10-19T12:34:56.789Z"
    )
)