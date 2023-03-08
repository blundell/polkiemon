package com.blundell.polkiemon

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class PokeApiRetrofitFactory {
    /**
     * This allows us to store responses in the http cache, even if the server doesn't send the headers itself
     * (Reasoning: The pokemon data we are using changes rarely.)
     */
    object CacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(14, TimeUnit.DAYS)
                .build()
            return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    companion object {
        fun create(cacheDir: File): Retrofit {
            val httpClient = OkHttpClient.Builder()
                .cache(Cache(File(cacheDir, "pokeapi-http-cache"), (5 * 1024L * 1024L))) // 5MB
                .addNetworkInterceptor(CacheInterceptor)
                .build()
            return Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .add(UrlAdapter())
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
        }
    }
}
