package com.example.rickandmortyapi

import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmortyapi.adapter.CharacterAdapter
import com.example.rickandmortyapi.databinding.ActivityCharacterBinding
import com.example.rickandmortyapi.event.CharacterListEvent
import com.example.rickandmortyapi.model.Character
import com.example.rickandmortyapi.util.onQueryTextChanged
import com.example.rickandmortyapi.viewmodel.CharacterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CharacterActivity : AppCompatActivity(), CharacterAdapter.OnCharacterClickListener {

    private val viewModel by viewModels<CharacterViewModel>()
    private val myAdapter by lazy { CharacterAdapter(this) }
    private lateinit var binding: ActivityCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        networkConnection()
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
                networkConnection()
            }.collectLatest {
                myAdapter.submitData(it)
            }
        }
    }

    private fun networkConnection() {
        val connectionManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectionManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
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
            "PlEASE CHECK NETWORK CONNECTION",
            Toast.LENGTH_SHORT
        )
            .setAction("Ok") {

            }
            .setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.RED)
        val textView = snackBarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun itemClicked(character: Character, position: Int) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("CHARACTERS", character.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_fragment, menu)
        val searchItem = menu?.findItem(R.id.search_View)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Type a character name"
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

