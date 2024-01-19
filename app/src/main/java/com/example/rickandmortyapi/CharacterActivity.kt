package com.example.rickandmortyapi

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmortyapi.adapter.CharacterAdapter
import com.example.rickandmortyapi.databinding.ActivityCharacterBinding
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.model.CharactersResponseEntity
import com.example.rickandmortyapi.util.Constants.EXTRA_CHARACTERS
import com.example.rickandmortyapi.util.NetworkUtils
import com.example.rickandmortyapi.util.onQueryTextChanged
import com.example.rickandmortyapi.viewmodel.CharacterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.M)
class CharacterActivity : AppCompatActivity(), CharacterAdapter.OnCharacterClickListener {

    private val viewModel by viewModels<CharacterViewModel>()
    private val myAdapter by lazy { CharacterAdapter(this) }
    private lateinit var binding: ActivityCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        showNetworkErrorIfRequired()
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            adapter = myAdapter
            layoutManager =
                LinearLayoutManager(this@CharacterActivity, LinearLayoutManager.VERTICAL, false)
            val divider = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(divider)
        }
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenCreated {
            viewModel.charactersFlow.catch {
                showNetworkErrorIfRequired()
            }.collectLatest {pagingData ->
                myAdapter.submitData(pagingData)
            }
        }
    }

    private fun showNetworkErrorIfRequired() {
        if (NetworkUtils.isConnected(this)) {
            setUpRecyclerView()
            initViewModel()
        } else {
            setUpRecyclerView()
            initViewModel()
            snackBar()
        }
    }

    private fun snackBar() {
        val snackBar = Snackbar.make(
            findViewById(R.id.frameLayout),
            getString(R.string.network_connection),
            Toast.LENGTH_SHORT
        )
            .setAction(getString(R.string.ok)) {

            }
            .setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.RED)
        val textView = snackBarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun itemClicked(character: CharactersResponseEntity, position: Int) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra(EXTRA_CHARACTERS, character.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_fragment, menu)
        val searchItem = menu?.findItem(R.id.search_View)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.character_name)
        searchView.onQueryTextChanged {
            performSearchEvent(it)
        }
        super.onCreateOptionsMenu(menu)
        return true
    }

    private fun performSearchEvent(characterName: String) {
        viewModel.onEvent(CharacterListEvent.GetAllCharactersByName(characterName))
    }
}

