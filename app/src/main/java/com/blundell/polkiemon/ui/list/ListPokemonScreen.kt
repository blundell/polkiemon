package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blundell.polkiemon.list.ListPokemonState.*
import com.blundell.polkiemon.list.ListPokemonViewModel
import com.blundell.polkiemon.ui.theme.PolkiemonTheme

@Composable
fun ListPokemonScreen(
    model: ListPokemonViewModel,
    onNavigateToPokemon: (String) -> Unit
) {
    PolkiemonTheme {
        when (val state = model.state.collectAsStateWithLifecycle().value) {
            Empty -> EmptyView()
            is Failure -> ErrorView(errorMessage = state.errorMessage)
            Loading -> LoadingView()
            is Success -> ListPokemonView(
                input = model,
                pokemon = state.pokemon,
                onNavigateToPokemon = onNavigateToPokemon
            )
        }
    }
}

@Composable
fun EmptyView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text(text = "Team Rocket have taken all the Pokemon!")
    }
}

@Composable
fun LoadingView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Loading")
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorView(errorMessage: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Oh no, we've dropped all our Poke-balls!")
            Text(text = "Technical issue: [${errorMessage}].")
        }
    }
}
