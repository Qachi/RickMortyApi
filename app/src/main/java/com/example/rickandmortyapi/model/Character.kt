package com.example.rickandmortyapi.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Character(
    val id: Int,
    val name: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val image: String,
    val created: String,
) : Parcelable