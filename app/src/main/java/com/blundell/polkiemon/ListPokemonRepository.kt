package com.blundell.polkiemon

import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ListPokemonRepository(
    private val dataSource: AllPokemonDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val logger: Logger = VoidLogger,
) {

    fun getAllPokemon(): Flow<List<PokemonListItem>> = flow {
        val result = dataSource.fetchAllPokemon()
        if (result.isSuccess) {
            val apiPokemonCollection = result.getOrThrow()
            logger.d("Got pokemon collection.")
            val allPokemon = apiPokemonCollection.results
                .map {
                    PokemonListItem(it.name, Uri.parse(it.url.toString()))
                }
                .sortedBy { it.name }
            emit(allPokemon)
        } else {
            logger.d("Data source failed [${result.exceptionOrNull()!!.message}].")
            error("Error fetching Pokemon. Do you have an internet connection?")
        }
    }.flowOn(dispatcher)
}
