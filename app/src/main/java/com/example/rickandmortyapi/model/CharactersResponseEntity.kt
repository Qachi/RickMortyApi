package com.example.rickandmortyapi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "characters_table")
data class CharactersResponseEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val image: String,
    val created: String,
    ) {
    fun toCharacter() = Character(
        id = id,
        name = name,
        species = species,
        gender = gender,
        origin = origin,
        location = location,
        image = image,
        created = created,
        )
}