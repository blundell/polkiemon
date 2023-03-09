package com.blundell.polkiemon.details

import com.blundell.polkiemon.DatabasePokemonDataSource
import com.blundell.polkiemon.NetworkPokemonDataSource
import com.blundell.polkiemon.logging.Logger
import com.blundell.polkiemon.logging.VoidLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PokemonDetailsRepository(
    private val networkDataSource: NetworkPokemonDataSource,
    private val databaseDataSource: DatabasePokemonDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val logger: Logger = VoidLogger,
) {

    fun getPokemon(name: String): Flow<PokemonDetails> = flow {
        // If I'd continued to do the work to save the details to the database
        // fetchPokemon will change to return all the data we save from the DB
        val dbPokemon = databaseDataSource.fetchPokemon(name)
        if (dbPokemon.isSuccess) {
            // send the first data we've got
            val (id, dbName, imageUrl) = dbPokemon.getOrThrow()
            logger.d("$name/$id got DB initial pokemon details.")
            emit(PokemonDetails(dbName, imageUrl, "???", "???", "???", "???"))
            // then hit the network
            val apiPokemonDetails = networkDataSource.fetchPokemon(id).getOrElse {
                val exceptionMessage = it.message
                logger.d("$name/$id data source failed [$exceptionMessage].")
                // In case of an network error, we give a delay to emitting so the previous emit is consumed
                // Alternative solutions include
                // - changing from StateFlow
                // - not emitting an error since we have valid data
                delay(300)
                error("No internet connection.")
            }
            logger.d("Got $name/$id network pokemon details.")
            // then save the network data
            // Not done this, but done for the list screen
//            databaseDataSource.savePokemon(apiPokemonDetails)
//            logger.d("Saved $name/$id Pokemon to the DB.")
            // then send the second data we saved
            emit(
                PokemonDetails(
                    apiPokemonDetails.name,
                    apiPokemonDetails.sprites.other.officialArtwork.svgUrl,
                    if (apiPokemonDetails.order == -1) "???" else "${apiPokemonDetails.order}",
                    apiPokemonDetails.stats.find { it.stat.name == "hp" }!!.baseStat,
                    "${apiPokemonDetails.height}",
                    "${apiPokemonDetails.weight}",
                )
            )
        }
    }.flowOn(dispatcher)
}
