package com.blundell.polkiemon.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blundell.polkiemon.list.PokemonListItem

@Composable
fun PokemonCard(item: PokemonListItem, onClick: (name: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(item.name) }
    ) {
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
}
