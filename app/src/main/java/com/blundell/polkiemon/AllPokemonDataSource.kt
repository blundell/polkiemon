package com.blundell.polkiemon

import retrofit2.Response

class DatabasePokemonDataSource(
    private val database: PolkiemonDatabase,
) {
    fun fetchPokemon(range: IntRange): List<EntityPokemon> {
        return database.pokemonDao().findByIdRange(range.first, range.count())
    }

    fun savePokemon(pokemon: List<EntityPokemon>) {
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
                offset = range.first, // TODO pagination
            ).toResult()
        } catch (e: Exception) {
            logger.d("Exception from retrofit ${e.message}.")
            return Result.failure(IllegalStateException("[400] Likely malformed response.", e))
        }
    }

    private fun <T> Response<T>.toResult(): Result<T> {
        val code = this.code()
        if (this.isSuccessful) {
            logger.d("fetchAllPokemon() network success [$code].")
            return Result.success(body()!!)
        } else {
            logger.d("fetchAllPokemon() network error [$code], ${errorBody()}.")
            return Result.failure(IllegalStateException("[$code]"))
        }
    }

}