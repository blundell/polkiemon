package com.blundell.polkiemon

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.ToJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URI
import java.net.URL

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemon(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Response<ApiPokemonCollection>

    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(
        @Path("id") id: Int,
    ): Response<ApiPokemonDetails>
}

data class ApiPokemonCollection(
    val count: Int,
    val next: String?,
    val previous: String?,
    @Json(name = "results") val pokemon: List<ApiPokemon>,
)

data class ApiPokemon(
    val name: String,
    @Json(name = "url") val moreInfoUrl: URL,
)

data class ApiPokemonDetails(
    val name: String,
    val order: Int,
    val height: Int,
    val weight: Int,
    val sprites: ApiSprites,
    val stats: List<ApiStatInfo>
)

data class ApiSprites(val other: ApiOther)

data class ApiOther(@Json(name = "official-artwork") val officialArtwork: ApiOfficialArtwork)

data class ApiOfficialArtwork(@Json(name = "front_default") val svgUrl: String)

data class ApiStatInfo(@Json(name = "base_stat") val baseStat: String, val effort: Int, val stat: ApiStat)
data class ApiStat(val name: String, val url: String)

/**
 * Uses URI internally to handle encoding/decoding
 * https://developer.android.com/reference/java/net/URL
 */
class UrlAdapter {
    @FromJson
    fun fromJson(jsonVal: String): URL = URI.create(jsonVal).toURL()

    @ToJson
    fun toJson(value: URL): String = value.toURI().toString()
}
