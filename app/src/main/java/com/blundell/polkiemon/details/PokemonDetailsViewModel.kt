package com.blundell.polkiemon.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

class PokemonDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    val name: String = checkNotNull(savedStateHandle["name"])

    init {
        println("XXX Details for $name")
    }

}
