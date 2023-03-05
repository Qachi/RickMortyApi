package com.example.rickandmortyapi.model

data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?

)