@file:OptIn(ExperimentalCoroutinesApi::class)

package com.blundell.polkiemon.details

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.blundell.polkiemon.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class PokemonDetailsRepositoryTest {

    @Test
    fun `UI model creation success when database initially populated and no network`() = runTest {
        val repo = PokemonDetailsRepository(
            NetworkPokemonDataSource(FakeExceptionPokeApiService("No network connection.")),
            DatabasePokemonDataSource(FakeSuccessDatabase(DB_JIGGLYPUFF)),
            Dispatchers.Unconfined,
        )
        var exceptionThrown = false

        val results = repo.getPokemon(NAME_JIGGLYPUFF)
            .catch {
                exceptionThrown = true
                assertEquals("No internet connection.", it.message)
            }
            .toList()

        assertEquals(UI_JIGGLYPUFF_INITIAL, results[0])
        assertTrue("Expected a network connection issue after initial DB emit.", exceptionThrown)
    }

    @Test(expected = IllegalStateException::class)
    fun `UI model creation success when database initially populated and api failure`() = runTest {
        val repo = PokemonDetailsRepository(
            NetworkPokemonDataSource(FakeErrorPokeApiService(500, "Server Down")),
            DatabasePokemonDataSource(FakeSuccessDatabase(DB_JIGGLYPUFF)),
            Dispatchers.Unconfined,
        )

        repo.getPokemon(NAME_JIGGLYPUFF).take(2).toList()
    }

    @Test
    fun `UI model creation success when database initially populated and api success`() = runTest {
        val repo = PokemonDetailsRepository(
            NetworkPokemonDataSource(FakeSuccessPokeApiService(API_JIGGLYPUFF)),
            DatabasePokemonDataSource(FakeSuccessDatabase(DB_JIGGLYPUFF)),
            Dispatchers.Unconfined,
        )

        val results = repo.getPokemon(NAME_JIGGLYPUFF).take(2).toList()

        assertEquals(UI_JIGGLYPUFF_INITIAL, results[0])
        assertEquals(UI_JIGGLYPUFF_FULL, results[1])
    }

    // When we add saving to database there would be more tests here

    private companion object {
        const val NAME_JIGGLYPUFF = "Jigglypuff"
        val API_JIGGLYPUFF = ApiPokemonDetails(
            name = "Jigglypuff",
            order = 71,
            height = 5,
            weight = 55,
            sprites = ApiSprites(
                ApiOther(
                    ApiOfficialArtwork(
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/39.png"
                    )
                )
            ),
            stats = listOf(
                ApiStatInfo(
                    baseStat = "115",
                    effort = 1,
                    ApiStat(
                        name = "hp",
                        url = "https://pokeapi.co/api/v2/stat/1/",
                    )
                )
            )
        )
        val DB_JIGGLYPUFF = EntityPokemon(
            id = 39,
            name = "Jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png",
        )
        val UI_JIGGLYPUFF_INITIAL = PokemonDetails(
            name = "Jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png",
            order = "???",
            hp = "???",
            height = "???",
            weight = "???",
        )
        val UI_JIGGLYPUFF_FULL = PokemonDetails(
            name = "Jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/39.png",
            order = "71",
            hp = "115",
            height = "5",
            weight = "55",
        )
    }

    private class FakeSuccessPokeApiService(val apiPokemonDetails: ApiPokemonDetails) : PokeApiService {
        override suspend fun getPokemon(limit: Int, offset: Int): Response<ApiPokemonCollection> {
            throw IllegalStateException("Unsupported here.")
        }

        override suspend fun getPokemonDetails(id: Int): Response<ApiPokemonDetails> {
            return Response.success(apiPokemonDetails)
        }
    }

    private class FakeErrorPokeApiService(val errorCode: Int, val errorMsg: String) : PokeApiService {
        override suspend fun getPokemon(limit: Int, offset: Int): Response<ApiPokemonCollection> {
            throw IllegalStateException("Unsupported here.")
        }

        override suspend fun getPokemonDetails(id: Int): Response<ApiPokemonDetails> {
            return Response.error(errorCode, errorMsg.toResponseBody("text/html".toMediaType()))
        }
    }

    private class FakeExceptionPokeApiService(val errorMsg: String) : PokeApiService {
        override suspend fun getPokemon(limit: Int, offset: Int): Response<ApiPokemonCollection> {
            throw IllegalStateException(errorMsg)
        }

        override suspend fun getPokemonDetails(id: Int): Response<ApiPokemonDetails> {
            throw IllegalStateException("Unsupported here.")
        }
    }

    private class FakeSuccessDatabase(val pokemon: EntityPokemon) : PolkiemonDatabase() {
        override fun pokemonDao(): PokemonDao {
            return object : PokemonDao {
                override suspend fun findByIdRange(startId: Int, count: Int): List<EntityPokemon> = throw IllegalStateException("Not supported.")
                override suspend fun findByName(name: String): EntityPokemon = pokemon
                override suspend fun insertAll(vararg pokemon: EntityPokemon) = Unit
            }
        }

        override fun clearAllTables() = Unit

        override fun createInvalidationTracker(): InvalidationTracker {
            return InvalidationTracker(this, "ignoredTableName")
        }

        override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
            throw java.lang.IllegalStateException("Calling this is unsupported/unexpected.")
        }
    }
}
