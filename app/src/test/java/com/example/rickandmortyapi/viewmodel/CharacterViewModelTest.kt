package com.example.rickandmortyapi.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.rickandmortyapi.MainCoroutineRule
import com.example.rickandmortyapi.adapter.CharacterAdapter
import com.example.rickandmortyapi.data.listOfCharacters
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.repositories.FakeRickMortyRepository
import com.example.rickandmortyapi.util.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CharacterViewModelTest {

    // 1. All character fetched if search query is empty
    // 2. Characters with name is fetched if search query is passed

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeRickMortyRepository: FakeRickMortyRepository
    private lateinit var sut: CharacterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fakeRickMortyRepository = FakeRickMortyRepository()
        fakeRickMortyRepository.setReturnValue(
            flowOf(PagingData.from(listOfCharacters))
        )
        // Define the filtered list for a specific query (e.g., "Rick")
        val filteredCharacters =
            listOfCharacters.filter { it.name.contains(Constants.CHARACTER_QUERY) }
        fakeRickMortyRepository.setSearchResponse(Constants.CHARACTER_QUERY, filteredCharacters)
        sut = CharacterViewModel(fakeRickMortyRepository)
    }

    @Test
    fun fetchAllCharactersIfSearchQueryIsEmpty() = runTest {
        val differ = AsyncPagingDataDiffer(
            diffCallback = CharacterAdapter.diffUtil,
            updateCallback = ListUpdateTestCallback(),
            workerDispatcher = Dispatchers.Main
        )
        // Collect the first item from charactersFlow
        val pagingData = sut.charactersFlow.first()
        differ.submitData(pagingData)
        advanceUntilIdle()
        assertEquals(listOfCharacters.map { it.id }, differ.snapshot().items.map { it.id })
    }

    @Test
    fun fetchCharacterIfSearchQueryIsPassed() = runTest {
        // Set up the differ to handle the PagingData
        val differ = AsyncPagingDataDiffer(
            diffCallback = CharacterAdapter.diffUtil,
            updateCallback = ListUpdateTestCallback(),
            workerDispatcher = Dispatchers.Main
        )
        // Set the search query in the ViewModel
        sut.onEvent(CharacterListEvent.GetAllCharactersByName(Constants.CHARACTER_QUERY))

        // Collect from charactersFlow and submit to the differ
        val pagingData = sut.charactersFlow.first()
        differ.submitData(pagingData)
        advanceUntilIdle()

        // Check if the filtered list only contains characters with the name "Rick"
        val expectedIds =
            listOfCharacters.filter { it.name.contains(Constants.CHARACTER_QUERY) }.map { it.name }
        assertEquals(expectedIds, differ.snapshot().items.map { it.name })
    }

    class ListUpdateTestCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}


