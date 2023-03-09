package com.blundell.polkiemon.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import com.blundell.polkiemon.*
import com.blundell.polkiemon.list.ListPokemonState.*
import com.blundell.polkiemon.logging.AndroidLogger
import com.blundell.polkiemon.logging.VoidLogger
import com.blundell.polkiemon.ui.ImageLoaderFactory
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

enum class ListState {
    IDLE,
    LOADING,
    PAGINATING,
    ERROR,
}

class ListPokemonViewModel(
    application: Application
) : AndroidViewModel(application) {

    // You could have a backing field here to ensure the state cannot be updated from outside the VM,
    // but I find that the extra code is not worth the maintenance over an agreed convention
    val screenState: MutableStateFlow<ListPokemonState> = MutableStateFlow(Empty)

    private val repository: ListPokemonRepository

    // Could have used the JetPack paging library
    private var page by mutableStateOf(1)
    var morePokemon by mutableStateOf(false)

    // I would like to have a singular UI state exposed, but skipping for time
    var listState by mutableStateOf(ListState.IDLE)

    /**
     * I'm doing all object instantiation here, but in a production app
     * you would likely use a dependency injection framework for more flexibility.
     * Also could have used a ViewModelFactory.
     */
    init {
        // could be done in the application class
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

    fun loadAllPokemon() {
        if (listState != ListState.IDLE) {
            return
        }
        if (page != 1 && !morePokemon) {
            return
        }
        if (page == 1) {
            listState = ListState.LOADING
            screenState.value = Loading
        } else {
            listState = ListState.PAGINATING
        }
        val range: IntRange = ((page * PER_PAGE) - PER_PAGE) until (page * PER_PAGE)
        repository.getPokemon(range)
            .onEach {
                if (it.isEmpty()) {
                    screenState.value = Empty
                } else {
                    morePokemon = it.size == PER_PAGE // The total per request, if less too near the end to paginate
                    val current = screenState.value
                    if (current is Success) {
                        screenState.value = Success(current.combine(it))
                    } else {
                        screenState.value = Success(it)
                    }
                    listState = ListState.IDLE
                    if (morePokemon) {
                        page++
                    }
                }
            }
            .catch {
                listState = ListState.ERROR
                // Only change the state to failure if we haven't been successful
                // This allows the user to browse the cached data rather than
                // change to the '404' screen
                // With more time a better UX would be to have a more subtle UI for errors
                // so that it wasn't just as screen swap
                if (screenState.value !is Success) {
                    screenState.value = Failure(it.message ?: "Unrecognised failure.")
                }
            }
            .launchIn(viewModelScope)
    }

    private fun Success.combine(other: List<PokemonListItem>): List<PokemonListItem> {
        return pokemon.toMutableList().also { it.addAll(other) }
    }

    fun onRefresh() {
        onCleared()
        loadAllPokemon()
        // We don't restore their position in the list, as a refresh starts from the beginning/top
    }

    override fun onCleared() {
        page = 1
        listState = ListState.IDLE
        morePokemon = false
        screenState.value = Empty
        super.onCleared()
    }

    companion object {
        private const val PER_PAGE = 60
    }
}

data class PokemonListItem(val name: String, val imageUrl: URL)
