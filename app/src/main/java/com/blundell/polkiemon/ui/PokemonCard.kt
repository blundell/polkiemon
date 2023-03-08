package com.blundell.polkiemon.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blundell.polkiemon.PokemonListItem

@Composable
fun PokemonCard(item: PokemonListItem) {
    Row(
        modifier = Modifier.padding(all = 8.dp)
    ) {
        PokemonCircularImage(
            imageUrl = item.imageUrl,
            contentDescription = "It's ${item.name}.",
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.name,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
