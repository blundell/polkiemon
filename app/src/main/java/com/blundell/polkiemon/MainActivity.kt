package com.blundell.polkiemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.blundell.polkiemon.ui.ListPokemon

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ListPokemonViewModel by viewModels()
        setContent {
            ListPokemon(model = viewModel)
        }
    }
}
