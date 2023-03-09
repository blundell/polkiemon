package com.blundell.polkiemon.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blundell.polkiemon.details.PokemonDetailsState
import com.blundell.polkiemon.details.PokemonDetailsViewModel
import com.blundell.polkiemon.ui.ErrorText
import com.blundell.polkiemon.ui.IdleText
import com.blundell.polkiemon.ui.LoadingText

@Composable
fun DetailsScreen(
    model: PokemonDetailsViewModel,
) {
    when (val state = model.screenState.collectAsStateWithLifecycle().value) {
        // All these views could be a lot more detailed / nicer UI
        is PokemonDetailsState.Error -> {
            // We have data, so we use it
            // With more time you could have a retry button and a nicer UX
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.previousDetails != null) {
                    PokemonDetailsView(details = state.previousDetails)
                }
                ErrorText(state.errorMessage)
            }
        }
        PokemonDetailsState.Idle -> IdleText(text = "IDLE")
        PokemonDetailsState.Loading -> LoadingText(text = "LOADING")
        is PokemonDetailsState.Success -> {
            PokemonDetailsView(details = state.details)
        }
    }
}

