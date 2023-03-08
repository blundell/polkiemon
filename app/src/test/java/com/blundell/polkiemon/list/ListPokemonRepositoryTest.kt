@file:OptIn(ExperimentalCoroutinesApi::class)

package com.blundell.polkiemon.list

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.blundell.polkiemon.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.net.URL

class ListPokemonRepositoryTest {

    @Test
    fun `UI model creation success when database populated`() = runTest {
        val repo = ListPokemonRepository(
            NetworkPokemonDataSource(FakeExceptionPokeApiService("Api not expected to be called for this test.")),
            DatabasePokemonDataSource(FakeSuccessDatabase(DB_JIGGLYPUFF)),
            Dispatchers.Unconfined,
        )

        val result = repo.getPokemon(ANY_RANGE).first()

        assertEquals(listOf(UI_JIGGLYPUFF), result)
    }

    @Test
    fun `UI model creation success when database empty and api success`() = runTest {
        val apiPokemonCollection = ApiPokemonCollection(
            count = 1,
            next = null,
            previous = null,
            pokemon = listOf(API_JIGGLYPUFF)
        )
        val repo = ListPokemonRepository(
            NetworkPokemonDataSource(FakeSuccessPokeApiService(apiPokemonCollection)),
            DatabasePokemonDataSource(FakeEmptyDatabase),
            Dispatchers.Unconfined,
        )

        val result = repo.getPokemon(ANY_RANGE).first()

        assertEquals(listOf(UI_JIGGLYPUFF), result)
    }

    @Test(expected = IllegalStateException::class)
    fun `UI model creation error when database empty and no network`() = runTest {
        val repo = ListPokemonRepository(
            NetworkPokemonDataSource(FakeExceptionPokeApiService("No internet connection.")),
            DatabasePokemonDataSource(FakeEmptyDatabase),
            Dispatchers.Unconfined,
        )

        repo.getPokemon(ANY_RANGE).first()
    }

    @Test(expected = IllegalStateException::class)
    fun `UI model creation error when database empty and api failure`() = runTest {
        val repo = ListPokemonRepository(
            NetworkPokemonDataSource(FakeErrorPokeApiService(500, "Server Down")),
            DatabasePokemonDataSource(FakeEmptyDatabase),
            Dispatchers.Unconfined,
        )

        repo.getPokemon(ANY_RANGE).first()
    }

    // Could have added a test for no network but network cache,
    // however in that scenario it'd have saved to the DB already as well
    // therefore covered by the DB test

    private companion object {
        val ANY_RANGE = 0..10
        val API_JIGGLYPUFF = ApiPokemon(
            name = "Jigglypuff",
            moreInfoUrl = URL("https://pokeapi.co/api/v2/pokemon/39/"),
        )
        val DB_JIGGLYPUFF = EntityPokemon(
            id = 39,
            name = "Jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png",
        )
        val UI_JIGGLYPUFF = PokemonListItem(
            name = "Jigglypuff",
            imageUrl = URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/39.png")
        )
    }

    private class FakeSuccessPokeApiService(val apiPokemonCollection: ApiPokemonCollection) : PokeApiService {
        override suspend fun getPokemon(limit: Int, offset: Int): Response<ApiPokemonCollection> {
            return Response.success(apiPokemonCollection)
        }

        override suspend fun getPokemonDetails(id: Int): Response<ApiPokemonDetails> {
            throw IllegalStateException("Unsupported here.")
        }
    }

    private class FakeErrorPokeApiService(val errorCode: Int, val errorMsg: String = "Error") : PokeApiService {
        override suspend fun getPokemon(limit: Int, offset: Int): Response<ApiPokemonCollection> {
            return Response.error(errorCode, errorMsg.toResponseBody("text/html".toMediaType()))
        }

        override suspend fun getPokemonDetails(id: Int): Response<ApiPokemonDetails> {
            throw IllegalStateException("Unsupported here.")
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

    private object FakeEmptyDatabase : PolkiemonDatabase() {
        override fun pokemonDao(): PokemonDao {
            return object : PokemonDao {
                override fun getAll(): List<EntityPokemon> = emptyList()
                override fun findByIdRange(startId: Int, count: Int): List<EntityPokemon> = emptyList()
                override fun findById(id: Int): EntityPokemon = throw IllegalStateException("Not found $id.")
                override fun findByName(name: String): EntityPokemon = throw IllegalStateException("Not found $name.")
                override fun insertAll(vararg pokemon: EntityPokemon) = Unit
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

    private class FakeSuccessDatabase(val pokemon: EntityPokemon) : PolkiemonDatabase() {
        override fun pokemonDao(): PokemonDao {
            return object : PokemonDao {
                override fun getAll(): List<EntityPokemon> = emptyList()
                override fun findByIdRange(startId: Int, count: Int): List<EntityPokemon> = listOf(pokemon)
                override fun findById(id: Int): EntityPokemon = throw IllegalStateException("Not found $id.")
                override fun findByName(name: String): EntityPokemon = throw IllegalStateException("Not found $name.")
                override fun insertAll(vararg pokemon: EntityPokemon) = Unit
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
