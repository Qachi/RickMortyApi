package com.example.rickandmortyapi.model

data class CharactersDto(
    val id: Int,
    val name: String,
    val species: String,
    val gender: String,
    val origin: OriginDto,
    val location: LocationDto,
    val image: String,
    val created: String,
) {

    fun toCharactersResponseEntity() = CharactersResponseEntity(
        id = id,
        name = name,
        species = species,
        gender = gender,
        origin = origin.name,
        location = location.name,
        image = image,
        created = created,
    )
}