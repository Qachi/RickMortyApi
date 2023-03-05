package com.example.rickandmortyapi.model

import com.google.gson.annotations.SerializedName

data class CharactersResponseDto(
    @SerializedName("info")
    val info: InfoDto,

    @SerializedName("results")
    val results: List<CharactersDto>
)