package com.example.rickandmortyapi.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.testing.asSnapshot
import com.example.rickandmortyapi.MainCoroutineRule
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.repositories.FakeRickMortyRepository
import com.example.rickandmortyapi.util.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterViewModelTest {

    // 1. All character fetched if search query is empty
    // 2. Characters with name is fetched if search query is passed

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var sut: CharacterViewModel
    private lateinit var fakeRickMortyRepository: FakeRickMortyRepository
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private fun setShouldReturnNetwork(value: Boolean) {
        fakeRickMortyRepository.setShouldReturnNetwork(value)
    }

    @Before
    fun setUp() {
        fakeRickMortyRepository = FakeRickMortyRepository()
        sut = CharacterViewModel(fakeRickMortyRepository)
    }

    @Test
    fun insertCharacterIntoDB() = runTest {
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
        sut.insertCharacterIntoDB(character)
        val resource = fakeRickMortyRepository.getCharacterById(characterID)
        assertNotNull(resource)
        assertEquals(Status.SUCCESS, resource.status)
        val insertedCharacter = resource.data
        assertNotNull(insertedCharacter)
        assertEquals(characterID, insertedCharacter?.id)
    }

    @Test
    fun deleteCharacters() = runTest {
        val character1 =
            CharactersResponseEntity(
                id = 1,
                name = "Rick",
                species = "Human",
                gender = "Male",
                origin = "Earth",
                location = "Earth",
                image = "rick.png",
                created = "2023-10-18T12:34:56.789Z"
            )
        val character2 = CharactersResponseEntity(
            id = 2,
            name = "Morty",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "morty.png",
            created = "2023-10-19T12:34:56.789Z"
        )
        sut.insertCharacterIntoDB(character1)
        sut.insertCharacterIntoDB(character2)
        sut.deleteCharacters()

        // Verify that characters have been deleted
        val characterNameToDelete = "Rick"
        val page = 1
        val characters = fakeRickMortyRepository.getCharacters(characterNameToDelete, page)
        assertTrue(characters.status == Status.ERROR)
        assertEquals("No characters found", characters.message)
    }

    //Success Test Case
    @Test
    fun `getCharacter Success`() = testScope.run {
        val imageQuery = "Rick"
        val page = 1
        sut.getCharacters(imageQuery, page)
        sut.updatedResource.observeForever { resources ->
            assertEquals(Status.SUCCESS, resources.status)
            assertNotNull(resources.data)
        }
    }

    @Test
    fun `getCharacter Error`() = testScope.run {
        val imageQuery = "Rick"
        val page = 1
        sut.getCharacters(imageQuery, page)
        sut.updatedResource.observeForever { resource ->
            assertEquals(Status.ERROR, resource.status)
            assertNotNull(resource.data)
        }
    }

    //  verify how your ViewModel handles an error scenario where
    //  .....the repository does not return a network error
    // ... but still produces an error in the response.
    // (focuses on how your ViewModel handles non-network errors
    // that may arise during the data retrieval process. )
    @Test
    fun `getCharacter No Error`() = testScope.run {
        val imageQuery = "Rick"
        val page = 1
        setShouldReturnNetwork(false)
        sut.getCharacters(imageQuery, page)
        sut.updatedResource.observeForever { resource ->
            assertEquals(Status.SUCCESS, resource.status)
            assertNotNull(resource.data)
        }
    }

    //Having Error with the below TestCase (This job has not completed yet ERROR!)
    @Test
    fun testGetCharacterByName() = runTest {
        val characterName = "Rick"
        val character1 = CharactersResponseEntity(
            id = 1,
            name = "Rick",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "rick.png",
            created = "2023-10-18T12:34:56.789Z"
        )
        val character2 = CharactersResponseEntity(
            id = 2,
            name = "Morty",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "morty.png",
            created = "2023-10-19T12:34:56.789Z"
        )
        val character3 = CharactersResponseEntity(
            id = 3,
            name = "Rick",
            species = "Human",
            gender = "Male",
            origin = "Earth",
            location = "Earth",
            image = "rick2.png",
            created = "2023-10-20T12:34:56.789Z"
        )
        // Insert characters with matching name
        sut.insertCharacterIntoDB(character1)
        sut.insertCharacterIntoDB(character3)

        // Insert a character with a different name
        sut.insertCharacterIntoDB(character2)

        // Assert that characters contain expected items
        assertEquals(expected, character3)
    }
}