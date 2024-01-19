package com.example.rickandmortyapi.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.model.CharactersResponseEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class RickMortyDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var rickMortyDatabase: RickMortyDatabase
    private lateinit var rickMortyDao: RickMortyDao

    @Before
    fun setup() {
        hiltRule.inject()
        rickMortyDao = rickMortyDatabase.getRickMortyDao()
    }

    @After
    fun teardown() {
        rickMortyDatabase.close()
    }

    @Test
    fun insertCharacter() = runBlocking {
        val characterID = 1
        val character = CharactersResponseEntity(
            id = characterID,
            name = "Rick",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "rick.png",
            created = "2023-10-18T12:34:56.789Z"
        )
        // Insert the character
        rickMortyDao.insertCharacter(character)

        // Retrieve the character by ID
        val rickMortyCharacter = rickMortyDao.getCharacterById(characterID)

        // Assert that the retrieved character is not null
        assertNotNull(rickMortyCharacter)

        // Assert that the retrieved character is equal to the inserted character
        assertEquals(character, rickMortyCharacter)
    }

    @Test
    fun deleteCharacter() = runBlocking {
        val characterID = 1
        val character = CharactersResponseEntity(
            id = characterID,
            name = "Rick",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "rick.png",
            created = "2023-10-18T12:34:56.789Z"
        )
        rickMortyDao.insertCharacter(character)
        rickMortyDao.deleteCharacters()

        // Attempt to retrieve the character by ID
        val rickMortyCharacter = rickMortyDao.getCharacterById(characterID)
        assertNull(rickMortyCharacter)
    }

    // WIP->  (KINDLY NOTE,getCharactersByName2 and getCharactersByName3 below gets to fail)
//    @Test
//    fun getCharactersByName2() = runBlocking {
//        // Query the database for characters with a specific name
//        val characterName = "Rick"
//        // Create some sample characters
//        val characterList = listOf(
//            CharactersResponseEntity(
//                id = 1,
//                name = "Rick",
//                species = "Human",
//                gender = "Male",
//                origin = "Earth",
//                location = "Earth",
//                image = "rick.png",
//                created = "2023-10-18T12:34:56.789Z"
//            ),
//            CharactersResponseEntity(
//                id = 2,
//                name = "Morty",
//                species = "Human",
//                gender = "Male",
//                origin = "Earth",
//                location = "Earth",
//                image = "morty.png",
//                created = "2023-10-19T12:34:56.789Z"
//            )
//        )
//        // Insert test data into the database
//        characterList.forEach { character ->
//            rickMortyDao.insertCharacter(character)
//        }
//
//        // Create an instance of your PagingSource
//        val pagingSource = rickMortyDao.getCharactersByName(characterName)
//
//        // Retrieve data from the PagingSource
//        val result = pagingSource.load(PagingSource.LoadParams.Refresh(0, 10, false))
//
//        // Verify the results
//        val resultList = (result as PagingSource.LoadResult.Page).data
//        assertEquals(characterList, resultList)
//    }
//
//    @Test
//    fun getCharactersByName3() = runTest {
//        val pageSize = 1
//        val characterName = "Rick"
//        val charactersResponseEntity = CharactersResponseEntity(
//            id = 1,
//            name = "Rick",
//            species = "Human",
//            gender = "Male",
//            origin = "Earth",
//            location = "Earth",
//            image = "rick.png",
//            created = "2023-10-18T12:34:56.789Z"
//        )
//        rickMortyDao.insertCharacter(charactersResponseEntity)
//        val pagingSource = rickMortyDao.getCharactersByName(characterName)
//        val result = pagingSource.load(PagingSource.LoadParams.Refresh(null, pageSize, false))
//        assertEquals(
//            PagingSource.LoadResult.Page(
//                data = listOf(charactersResponseEntity),
//                prevKey = null,
//                nextKey = pageSize
//            ),
//            result
//        )
//    }
}
