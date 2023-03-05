package com.example.rickandmortyapi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharactersResponseRemoteKey(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val prev: Int?,
    val next: Int?
)