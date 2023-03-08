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
    val screenState: MutableStateFlow<PokemonDetailsState> = MutableStateFlow(PokemonDetailsState.Idle)

    val name: String = checkNotNull(savedStateHandle[EXTRA_NAME])

    private val repository: PokemonDetailsRepository

    init {
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
        screenState.value = Loading
        repository.getPokemon(name)
            .onEach { screenState.value = PokemonDetailsState.Success(it) }
            .catch { screenState.value = PokemonDetailsState.Error(it.message ?: "Unrecognised failure.") }
            .launchIn(viewModelScope)
    }

    companion object {
        const val EXTRA_NAME = "name"
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
