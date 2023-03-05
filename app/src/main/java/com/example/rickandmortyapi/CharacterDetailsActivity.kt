package com.example.rickandmortyapi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortyapi.viewmodel.CharacterIdViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_character_details.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CharacterDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<CharacterIdViewModel>()
    private val id by lazy { intent.extras!!.getInt("CHARACTERS") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        initViewModel()

    }

    private fun initViewModel() {
        setActionBar()
        lifecycleScope.launchWhenCreated {
            viewModel.getCharacterById(id).catch {

            }.collectLatest {
                nameTxt.text = it.name
                speciesTxt.text = it.species
                genderTxt.text = it.gender
                originTxt.text = it.origin
                locationTxt.text = it.location
                createdTxt.text = it.created
                Picasso.get().load(it.image).into(userAvatar)
                supportActionBar?.title = it.name

            }

        }
    }

    private fun setActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}


