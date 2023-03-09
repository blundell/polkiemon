package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
    onNavigateToPokemon: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    val startPagination = remember {
        derivedStateOf {
            model.morePokemon && listState.lastNItemsVisible(10)
        }
    }
    LaunchedEffect(key1 = startPagination.value) {
        if (startPagination.value && model.listState == ListState.IDLE) {
            model.loadAllPokemon()
        }
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
            ListPaginationFooter(model)
        }
    }
}

/**
 * Left the UI here very simple, could be improved a lot
 */
@Composable
private fun ListPaginationFooter(model: ListPokemonViewModel) {
    when (model.listState) {
        ListState.IDLE -> {
            /** Don't show anything */
        }
        ListState.LOADING,
        ListState.PAGINATING -> {
            Text("LOADING")
        }
        ListState.ERROR -> {
            Text("ERROR")
        }
    }
}

/**
 * Returns true if the the list scroll state is in a position that the Nth item is visible.
 * n: the number of items from the end to start to return true for
 * Note: if no items are visible this will return false
 */
private fun LazyListState.lastNItemsVisible(n: Int): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -(n * 2)
    return lastVisibleItemIndex >= (layoutInfo.totalItemsCount - n)
}
