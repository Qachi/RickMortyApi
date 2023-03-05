package com.example.rickandmortyapi.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapi.model.CharactersResponseEntity

@Dao
interface RickyMortyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(list: List<CharactersResponseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharactersResponseEntity)

    @Query("SELECT * FROM characters_table WHERE name LIKE '%' || :characterName || '%' ORDER BY id ASC")
    fun getCharactersByName(characterName: String): PagingSource<Int, CharactersResponseEntity>

    @Query("SELECT * FROM characters_table WHERE id == :id LIMIT 1")
    fun getCharacter(id: Int): CharactersResponseEntity?

    @Query("DELETE FROM characters_table")
    suspend fun deleteCharacters()


}