package com.blundell.polkiemon

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blundell.polkiemon.ListPokemonState.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

sealed class ListPokemonState {
    object Empty : ListPokemonState()
    object Loading : ListPokemonState()
    data class Success(val pokemon: List<PokemonListItem>) : ListPokemonState()
    data class Failure(val errorMessage: String) : ListPokemonState()
}

class ListPokemonViewModel : ViewModel() {

    // You could have a backing field here to ensure the state cannot be updated from outside the VM,
    // but I find that the extra code is not worth the maintenance over an agreed convention
    val state: MutableStateFlow<ListPokemonState> = MutableStateFlow(Empty)

    private val repository: ListPokemonRepository

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(UrlAdapter())
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
        val apiService = retrofit.create(PokeApiService::class.java)
        val logger = if (BuildConfig.DEBUG) AndroidLogger else VoidLogger
        val dataSource = NetworkAllPokemonDataSource(apiService, logger)
        repository = ListPokemonRepository(dataSource, Dispatchers.IO, logger)
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

}

data class PokemonListItem(val name: String, val imageUrl: Uri)
