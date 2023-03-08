package com.blundell.polkiemon.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.blundell.polkiemon.*
import com.blundell.polkiemon.details.PokemonDetailsState.Loading
import com.blundell.polkiemon.logging.AndroidLogger
import com.blundell.polkiemon.logging.VoidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed class PokemonDetailsState {
    object Idle : PokemonDetailsState()
    object Loading : PokemonDetailsState()
    data class Success(val details: PokemonDetails) : PokemonDetailsState()
    data class Error(val errorMessage: String) : PokemonDetailsState()
}

class PokemonDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    // You could have a backing field here to ensure the state cannot be updated from outside the VM,
    // but I find that the extra code is not worth the maintenance over an agreed convention
    val state: MutableStateFlow<PokemonDetailsState> = MutableStateFlow(PokemonDetailsState.Idle)

    val name: String = checkNotNull(savedStateHandle["name"])

    private val repository: PokemonDetailsRepository

    init {
        println("XXX Details for $name")
        val cacheDir = application.cacheDir
        val apiService = PokeApiRetrofitFactory.create(cacheDir).create(PokeApiService::class.java)
        val logger = if (BuildConfig.DEBUG) AndroidLogger else VoidLogger
        val networkDataSource = NetworkPokemonDataSource(apiService, logger)
        val database = PolkiemonDatabase.getInstance(application)
        val databaseDataSource = DatabasePokemonDataSource(database)
        repository = PokemonDetailsRepository(networkDataSource, databaseDataSource, Dispatchers.IO, logger)
        loadPokemon()
    }

    private fun loadPokemon() {
        state.value = Loading
        repository.getPokemon(name)
            .onEach { state.value = PokemonDetailsState.Success(it) }
            .catch { state.value = PokemonDetailsState.Error(it.message ?: "Unrecognised failure.") }
            .launchIn(viewModelScope)
    }

}

data class PokemonDetails(
    val name: String,
    val imageUrl: String,
    val order: String,
    val hp: String,
    val height: String,
    val weight: String,
)
