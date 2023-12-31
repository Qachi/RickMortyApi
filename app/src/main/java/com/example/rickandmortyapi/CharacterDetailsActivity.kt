package com.example.rickandmortyapi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortyapi.databinding.ActivityCharacterDetailsBinding
import com.example.rickandmortyapi.viewmodel.CharacterDetailsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CharacterDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<CharacterDetailsViewModel>()
    private val id by lazy { intent.extras!!.getInt("CHARACTERS") } //Extract as constant
    private lateinit var binding: ActivityCharacterDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initViewModel()
    }

    private fun initViewModel() {
        setActionBar()
        lifecycleScope.launchWhenCreated {
            viewModel.getCharacterById(id).catch {

            }.collectLatest {
                binding.nameTxt.text = it.name
                binding.speciesTxt.text = it.species
                binding.genderTxt.text = it.gender
                binding.originTxt.text = it.origin
                binding.locationTxt.text = it.location
                binding.createdTxt.text = it.created
                Picasso.get().load(it.image).into(binding.userAvatar)
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