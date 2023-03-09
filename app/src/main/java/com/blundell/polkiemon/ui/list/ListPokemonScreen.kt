package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blundell.polkiemon.R
import com.blundell.polkiemon.list.ListPokemonState.*
import com.blundell.polkiemon.list.ListPokemonViewModel
import com.blundell.polkiemon.ui.ErrorText
import com.blundell.polkiemon.ui.LoadingText
import com.blundell.polkiemon.ui.theme.PolkiemonTheme

@Composable
fun ListPokemonScreen(
    model: ListPokemonViewModel,
    onNavigateToPokemon: (String) -> Unit
) {
    PolkiemonTheme {
        when (val state = model.screenState.collectAsStateWithLifecycle().value) {
            // All these views could be a lot more detailed / nicer UI
            Empty -> EmptyView()
            is Failure -> ErrorView(errorMessage = state.errorMessage)
            Loading -> LoadingView()
            is Success -> ListPokemonView(
                model = model,
                pokemon = state.pokemon,
                onNavigateToPokemon = onNavigateToPokemon
            )
        }
    }
}

@Composable
fun EmptyView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        ErrorText(text = "Team Rocket have taken all the Pokemon!")
    }
}

@Composable
fun LoadingView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoadingText(text = "Loading")
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorView(errorMessage: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ErrorText(text = "Oh no, we've dropped all our Poke-balls!")
            Spacer(modifier = Modifier.padding(16.dp))
            ErrorText(text = "Technical issue: [${errorMessage}].")
            Image(painter = painterResource(id = R.drawable.psyduck_404), contentDescription = "404, sad psyduck")
        }
    }
}

@Preview
@Composable
fun PreviewErrorView() {
    ErrorView(errorMessage = "Do you have an internet connection?")
}
