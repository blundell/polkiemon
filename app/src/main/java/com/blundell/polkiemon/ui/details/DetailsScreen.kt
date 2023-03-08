package com.blundell.polkiemon.ui.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blundell.polkiemon.details.PokemonDetailsState
import com.blundell.polkiemon.details.PokemonDetailsViewModel

@Composable
fun DetailsScreen(
    model: PokemonDetailsViewModel,
) {
    when (val state = model.screenState.collectAsStateWithLifecycle().value) {
        is PokemonDetailsState.Error -> Text(text = "ERROR ${state.errorMessage}")
        PokemonDetailsState.Idle -> Text(text = "IDLE")
        PokemonDetailsState.Loading -> Text(text = "LOADING")
        is PokemonDetailsState.Success -> PokemonDetailsView(details = state.details)
    }
}
