package com.example.rickandmortyapi.repository

import androidx.paging.PagingData
import com.example.rickandmortyapi.model.Character
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface RickMortyRepository {
    fun getCharacterByName(characterName: String):Flow<PagingData<Character>>

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RickMortyRepositoryModule{

    @Singleton
    @Binds
    abstract fun bindRickMortyRepository(repositoryImpl: RickMortyRepositoryImpl):RickMortyRepository
}