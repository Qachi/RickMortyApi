package com.example.rickandmortyapi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rickandmortyapi.dao.RickyMortyDao
import com.example.rickandmortyapi.dao.RickyMortyRemoteKeyDao
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.model.CharactersResponseRemoteKey

@Database(entities = [CharactersResponseEntity::class, CharactersResponseRemoteKey::class], version = 6)

abstract class RickMortyDatabase : RoomDatabase() {

    abstract fun getRickMortyDao(): RickyMortyDao
    abstract fun getRickyMortyRemoteKeyDao(): RickyMortyRemoteKeyDao

}