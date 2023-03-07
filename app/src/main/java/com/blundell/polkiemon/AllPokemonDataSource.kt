package com.blundell.polkiemon

import retrofit2.Response

interface AllPokemonDataSource {
    suspend fun fetchAllPokemon(): Result<ApiPokemonCollection>
}

class NetworkAllPokemonDataSource(
    private val pokeApiService: PokeApiService,
    private val logger: Logger = VoidLogger,
) : AllPokemonDataSource {
    override suspend fun fetchAllPokemon(): Result<ApiPokemonCollection> {
        try {
            return pokeApiService.getAllPokemon(
                limit = 30,
                offset = 0, // TODO pagination
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
