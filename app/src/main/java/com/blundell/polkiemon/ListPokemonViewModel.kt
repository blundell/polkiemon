package com.blundell.polkiemon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import com.blundell.polkiemon.ListPokemonState.*
import com.blundell.polkiemon.logging.AndroidLogger
import com.blundell.polkiemon.logging.VoidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.URL

sealed class ListPokemonState {
    object Empty : ListPokemonState()
    object Loading : ListPokemonState()
    data class Success(val pokemon: List<PokemonListItem>) : ListPokemonState()
    data class Failure(val errorMessage: String) : ListPokemonState()
}

interface ListPokemonInput {
    fun onRefresh()
}

class ListPokemonViewModel(
    application: Application
) : AndroidViewModel(application), ListPokemonInput {

    // You could have a backing field here to ensure the state cannot be updated from outside the VM,
    // but I find that the extra code is not worth the maintenance over an agreed convention
    val state: MutableStateFlow<ListPokemonState> = MutableStateFlow(Empty)

    private val repository: ListPokemonRepository

    /**
     * I'm doing all object instantiation here, but in a production app
     * you would likely use a dependency injection framework for more flexibility.
     * Also could have used a ViewModelFactory.
     */
    init {
        Coil.setImageLoader(ImageLoaderFactory(application))
        val cacheDir = application.cacheDir
        val apiService = PokeApiRetrofitFactory.create(cacheDir).create(PokeApiService::class.java)
        val logger = if (BuildConfig.DEBUG) AndroidLogger else VoidLogger
        val networkDataSource = NetworkPokemonDataSource(apiService, logger)
        val database = PolkiemonDatabase.getInstance(application)
        val databaseDataSource = DatabasePokemonDataSource(database)
        repository = ListPokemonRepository(networkDataSource, databaseDataSource, Dispatchers.IO, logger)
        loadAllPokemon()
    }

    private fun loadAllPokemon() {
        state.value = Loading
        repository.getAllPokemon()
            .onEach {
                if (it.isEmpty()) {
                    state.value = Empty
                } else {
                    state.value = Success(it)
                }
            }
            .catch { state.value = Failure(it.message ?: "Unrecognised failure.") }
            .launchIn(viewModelScope)
    }

    override fun onRefresh() {
        loadAllPokemon()
        // We don't store their position in the list, as a refresh starts from the beginning/top
    }
}

data class PokemonListItem(val name: String, val imageUrl: URL)
