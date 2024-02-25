package com.example.rickandmortyapi.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable
