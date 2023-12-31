package com.example.rickandmortyapi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapi.model.CharactersResponseRemoteKey

@Dao
interface RickyMortyRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharactersRemoteKey(list: List<CharactersResponseRemoteKey>)

    @Query("SELECT * FROM CharactersResponseRemoteKey WHERE id = :id")
    suspend fun getCharactersRemoteKey(id: Int): CharactersResponseRemoteKey

    @Query("DELETE FROM CharactersResponseRemoteKey")
    suspend fun deleteCharacter()
}