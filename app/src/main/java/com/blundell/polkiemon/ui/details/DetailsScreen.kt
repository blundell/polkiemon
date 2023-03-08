package com.blundell.polkiemon.ui.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.blundell.polkiemon.details.PokemonDetailsViewModel

@Composable
fun DetailsScreen(
    model: PokemonDetailsViewModel,
) {

    Text(text = "DETAILS TODO ${model.name}")

}
