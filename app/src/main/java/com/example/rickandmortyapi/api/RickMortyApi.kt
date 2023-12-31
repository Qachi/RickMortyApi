package com.example.rickandmortyapi.api

import com.example.rickandmortyapi.model.CharactersResponseDto
import com.example.rickandmortyapi.model.CharactersResponseEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("name") name: String,
        @Query("page") page: Int
    ): Response<CharactersResponseDto>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): Response<CharactersResponseEntity>
}