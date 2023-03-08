package com.blundell.polkiemon.details

import com.blundell.polkiemon.DatabasePokemonDataSource
import com.blundell.polkiemon.NetworkPokemonDataSource
import com.blundell.polkiemon.logging.Logger
import com.blundell.polkiemon.logging.VoidLogger
import kotlinx.coroutines.CoroutineDispatcher
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
        // TODO fetchPokemon will change to return all the data we save from the DB
        val dbPokemon = databaseDataSource.fetchPokemon(name)
        if (dbPokemon.isSuccess) {
            // send the first data we've got
            val (id, dbName, imageUrl) = dbPokemon.getOrThrow()
            emit(PokemonDetails(dbName, imageUrl, "???", "???", "???", "???"))
            // then hit the network
            val apiPokemonDetails = networkDataSource.fetchPokemon(id).getOrElse {
                val exceptionMessage = it.message
                logger.d("$name/$id data source failed [$exceptionMessage].")
                error("Error fetching Pokemon. Do you have an internet connection?")
            }
            logger.d("Got $name/$id network pokemon details.")
            // then save the network data TODO
//            databaseDataSource.savePokemon(apiPokemonDetails)
//            logger.d("Saved $name/$id Pokemon to the DB.")
            // then send the second data we saved
            emit(
                PokemonDetails(
                    apiPokemonDetails.name,
                    apiPokemonDetails.sprites.other.officialArtwork.svgUrl,
                    "${apiPokemonDetails.order}",
                    apiPokemonDetails.stats.find { it.stat.name == "hp" }!!.baseStat,
                    "${apiPokemonDetails.height}",
                    "${apiPokemonDetails.weight}",
                )
            )
        }
    }.flowOn(dispatcher)
}
