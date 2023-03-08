package com.blundell.polkiemon.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.blundell.polkiemon.details.PokemonDetails
import com.blundell.polkiemon.ui.PolkiemonHeader

@Composable
fun PokemonDetailsView(details: PokemonDetails) {
    Column {
        PolkiemonHeader()
        Column {
            Row {
                HeroImage(
                    imageUrl = details.imageUrl,
                    contentDescription = "A ${details.name}."
                )
                Column {
                    Row {
                        Text("Name")
                        Text(details.name)
                    }
                    Row {
                        Text("Order")
                        Text(details.order)
                    }
                }
            }

            Column {
                Row {
                    Text("HP")
                    Text(details.hp)
                }
                Row {
                    Text("Height")
                    Text(details.height)
                }
                Row {
                    Text("Weight")
                    Text(details.weight)
                }
            }
        }
    }
}
