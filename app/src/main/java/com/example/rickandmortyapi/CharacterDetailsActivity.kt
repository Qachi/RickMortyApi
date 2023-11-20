package com.example.rickandmortyapi

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortyapi.databinding.ActivityCharacterDetailsBinding
import com.example.rickandmortyapi.util.Constants
import com.example.rickandmortyapi.util.Status
import com.example.rickandmortyapi.viewmodel.CharacterDetailsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CharacterDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<CharacterDetailsViewModel>()
    private val id by lazy { intent.extras!!.getInt(Constants.CHARACTERS) }
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
                // Handle errors if needed
            }.collectLatest { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val character = resource.data
                        binding.nameTxt.text = character?.name
                        binding.speciesTxt.text = character?.species
                        binding.genderTxt.text = character?.gender
                        binding.originTxt.text = character?.origin
                        binding.locationTxt.text = character?.location
                        binding.createdTxt.text = character?.created
                        Picasso.get().load(character?.image).into(binding.userAvatar)
                        supportActionBar?.title = character?.name
                    }

                    Status.ERROR -> {
                        // Handle the error case
                        Toast.makeText(
                            this@CharacterDetailsActivity,
                            resource.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Status.LOADING -> {
                        // Handle the loading state if applicable
                        Log.d(TAG, "Loading person data...")
                    }
                }
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