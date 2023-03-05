package com.example.rickandmortyapi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rickandmortyapi.repository.RickMortyRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CharacterIdViewModel @Inject constructor(private val  rickMortyRepository: RickMortyRepositoryImpl) : ViewModel(){

suspend fun getCharacterById(id:Int) = withContext(Dispatchers.IO){
    rickMortyRepository.getCharacter(id)
}

}