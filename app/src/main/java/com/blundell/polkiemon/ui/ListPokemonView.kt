package com.blundell.polkiemon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blundell.polkiemon.ListPokemonInput
import com.blundell.polkiemon.PokemonListItem

@Composable
fun ListPokemonView(listPokemonInput: ListPokemonInput, pokemon: List<PokemonListItem>) {
    Column {
        Header(listPokemonInput)
        PokemonList(pokemon)
    }
}

@Composable
private fun Header(input: ListPokemonInput) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface
            )
            .padding(6.dp)
    ) {
        Text(
            text = "Polkiemon",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { input.onRefresh() }
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_revert),
                contentDescription = "refresh"
            )
        }
    }
}

@Composable
private fun PokemonList(pokemon: List<PokemonListItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(pokemon) {
            PokemonCard(it)
        }
    }
}

@Preview
@Composable
fun PreviewHeader() {
    Header(input = object : ListPokemonInput {
        override fun onRefresh() = Unit
    })
}
