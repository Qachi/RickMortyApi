package com.example.rickandmortyapi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rickandmortyapi.dao.RickMortyDao
import com.example.rickandmortyapi.dao.RickyMortyRemoteKeyDao
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.model.CharactersResponseRemoteKey

@Database(entities = [CharactersResponseEntity::class,
    CharactersResponseRemoteKey::class],
    version = 6,
    exportSchema = false)

abstract class RickMortyDatabase : RoomDatabase() {

    abstract fun getRickMortyDao(): RickMortyDao
    abstract fun getRickyMortyRemoteKeyDao(): RickyMortyRemoteKeyDao
}