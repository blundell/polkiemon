package com.blundell.polkiemon

import android.content.Context
import androidx.room.*

@Database(entities = [EntityPokemon::class], version = 1)
abstract class PolkiemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    companion object {

        private lateinit var database: PolkiemonDatabase
        fun getInstance(context: Context): PolkiemonDatabase {
            if (this::database.isInitialized) {
                return database
            }
            return Room.databaseBuilder(
                context = context.applicationContext,
                klass = PolkiemonDatabase::class.java,
                name = "polkiemonDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also {
                    database = it
                }
        }
    }
}

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon LIMIT :count OFFSET :startId")
    suspend fun findByIdRange(startId: Int, count: Int): List<EntityPokemon>

    @Query("SELECT * FROM pokemon WHERE full_name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): EntityPokemon

    @Upsert
    suspend fun insertAll(vararg pokemon: EntityPokemon)

}

@Entity(tableName = "pokemon")
data class EntityPokemon(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "full_name") val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
)
