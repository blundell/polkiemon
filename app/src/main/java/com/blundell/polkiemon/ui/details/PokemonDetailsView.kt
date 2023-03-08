package com.blundell.polkiemon.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blundell.polkiemon.details.PokemonDetails
import com.blundell.polkiemon.ui.PolkiemonHeader

@Composable
fun PokemonDetailsView(details: PokemonDetails) {
    Column {
        PolkiemonHeader()
        Card(
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Text(
                        text = details.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    HeroImage(
                        imageUrl = details.imageUrl,
                        contentDescription = "A ${details.name}."
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                val tableData = listOf(
                    "Order" to details.order,
                    "HP" to details.hp,
                    "Height" to details.height,
                    "Weight" to details.weight,
                )
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(tableData) {
                        val (label, detail) = it
                        Row(Modifier.fillMaxWidth()) {
                            LabelLabel(label, 0.3f)
                            LabelDetail(detail, 0.7f)
                        }
                    }
                }
            }
        }
    }
}
