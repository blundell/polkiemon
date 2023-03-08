package com.blundell.polkiemon

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.URL

class ListPokemonRepository(
    private val networkDataSource: NetworkPokemonDataSource,
    private val databaseDataSource: DatabasePokemonDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val logger: Logger = VoidLogger,
) {

    fun getAllPokemon(): Flow<List<PokemonListItem>> = flow {
        // TODO account for pagination
        val range = 0..60
        val dbPokemon = databaseDataSource.fetchPokemon(range)
        if (dbPokemon.isNotEmpty()) {
            emit(dbPokemon.toPokemonListItems())
        } else {
            val apiCollection = networkDataSource.fetchPokemon(range).getOrElse {
                val exceptionMessage = it.message
                logger.d("ID[$range] data source failed [$exceptionMessage].")
                error("Error fetching Pokemon. Do you have an internet connection?")
            }
            logger.d("Got ID[$range] network pokemon collection.")
            val apiPokemon = apiCollection.pokemon.toEntityPokemon()
            databaseDataSource.savePokemon(apiPokemon)
            logger.d("Saved ID[$range] Pokemon to the DB.")
            emit(apiPokemon.toPokemonListItems())
        }
    }.flowOn(dispatcher)

    private fun List<ApiPokemon>.toEntityPokemon() = map {
        // API doesn't serve us image urls
        // Create our own
        // https://github.com/PokeAPI/pokeapi/issues/346
        // Ideally I'd wrap the PokeAPI server-side in something more mobile friendly
        val id = it.moreInfoUrl.toString().removeSuffix("/").split("/").last().toInt()
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        EntityPokemon(id, it.name, imageUrl)
    }

    private fun List<EntityPokemon>.toPokemonListItems() = map {
        PokemonListItem(it.name, URL(it.imageUrl))
    }
}
