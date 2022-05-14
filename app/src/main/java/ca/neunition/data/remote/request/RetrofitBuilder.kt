/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * The Retrofit instance for making API calls.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.request

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
object RetrofitBuilder {
    private val BASE_URL: String by lazy { "https://api.edamam.com/" }

    // For Moshi's annotations to work properly with Kotlin, add the KotlinJsonAdapterFactory in the
    // Moshi builder and then call build().
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    // OkHttp3 client for network timeouts
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // The Retrofit object with the Moshi converter.
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(client)
            .build()
    }

    // A Edamam Recipe API object that exposes the lazy-initialized Retrofit service.
    val edamamApiService: EdamamApiService by lazy {
        retrofitBuilder.create(EdamamApiService::class.java)
    }
}
