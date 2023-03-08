package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.blundell.polkiemon.list.ListPokemonViewModel
import com.blundell.polkiemon.list.ListState
import com.blundell.polkiemon.list.PokemonListItem
import com.blundell.polkiemon.ui.PolkiemonHeader

@Composable
fun ListPokemonView(
    model: ListPokemonViewModel,
    pokemon: List<PokemonListItem>,
    onNavigateToPokemon: (String) -> Unit,
) {
    Column {
        PolkiemonHeader(includeRefresh = true) {
            model.onRefresh()
        }
        PokemonList(model, pokemon, onNavigateToPokemon)
    }
}

@Composable
private fun PokemonList(
    model: ListPokemonViewModel,
    pokemon: List<PokemonListItem>,
    onNavigateToPokemon: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            model.morePokemon &&
                    (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -20) >=
                    (listState.layoutInfo.totalItemsCount - 10)
        }
    }
    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value && model.listState == ListState.IDLE)
            model.loadAllPokemon()
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(pokemon) {
            PokemonCard(it) { name ->
                onNavigateToPokemon(name)
            }
        }
        item(key = model.listState) {
            when (model.listState) {
                ListState.IDLE -> {
                    /** Don't show anything */
                }
                ListState.LOADING -> {
                    Text("LOADING")
                }
                ListState.PAGINATING -> {
                    Text("PAGINATING")
                }
                ListState.ERROR -> {
                    Text("ERROR")
                }
            }
        }
    }
}
