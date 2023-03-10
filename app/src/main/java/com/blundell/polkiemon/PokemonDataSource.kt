package com.blundell.polkiemon

import com.blundell.polkiemon.logging.Logger
import com.blundell.polkiemon.logging.VoidLogger
import retrofit2.Response

class DatabasePokemonDataSource(
    private val database: PolkiemonDatabase,
) {
    suspend fun fetchPokemon(range: IntRange): List<EntityPokemon> {
        return database.pokemonDao().findByIdRange(range.first, range.count())
    }

    suspend fun fetchPokemon(name: String): Result<EntityPokemon> {
        // At the moment the app will always return a Pokemon when this is called
        // That may not always be the case and error handling / Result failure could be added
        return Result.success(database.pokemonDao().findByName(name))
    }

    suspend fun savePokemon(pokemon: List<EntityPokemon>) {
        database.pokemonDao().insertAll(*pokemon.toTypedArray())
    }
}

class NetworkPokemonDataSource(
    private val pokeApiService: PokeApiService,
    private val logger: Logger = VoidLogger,
) {
    suspend fun fetchPokemon(range: IntRange): Result<ApiPokemonCollection> {
        try {
            return pokeApiService.getPokemon(
                limit = range.count(),
                offset = range.first,
            ).toResult()
        } catch (e: Exception) {
            logger.d("Exception from retrofit ${e.message}.")
            return Result.failure(IllegalStateException("[400] Likely malformed response.", e))
        }
    }

    suspend fun fetchPokemon(id: Int): Result<ApiPokemonDetails> {
        try {
            return pokeApiService.getPokemonDetails(id).toResult()
        } catch (e: Exception) {
            logger.d("Exception from retrofit ${e.message}.")
            return Result.failure(IllegalStateException("[400] Likely malformed response.", e))
        }
    }

    private fun <T> Response<T>.toResult(): Result<T> {
        val code = this.code()
        if (this.isSuccessful) {
            logger.d("fetchPokemon() network datasource success [$code].")
            return Result.success(body()!!)
        } else {
            logger.d("fetchPokemon() network datasource error [$code], ${errorBody()}.")
            return Result.failure(IllegalStateException("[$code]"))
        }
    }

}
