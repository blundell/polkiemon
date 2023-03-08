package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blundell.polkiemon.list.ListPokemonInput
import com.blundell.polkiemon.list.PokemonListItem
import com.blundell.polkiemon.ui.PolkiemonHeader

@Composable
fun ListPokemonView(
    input: ListPokemonInput,
    pokemon: List<PokemonListItem>,
    onNavigateToPokemon: (String) -> Unit,
) {
    Column {
        PolkiemonHeader(includeRefresh = true) {
            input.onRefresh()
        }
        PokemonList(pokemon, onNavigateToPokemon)
    }
}

@Composable
private fun PokemonList(
    pokemon: List<PokemonListItem>,
    onNavigateToPokemon: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(pokemon) {
            PokemonCard(it) { name ->
                onNavigateToPokemon(name)
            }
        }
    }
}
