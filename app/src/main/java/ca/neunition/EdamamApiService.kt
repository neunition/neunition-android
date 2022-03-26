package ca.neunition

import ca.neunition.data.model.api.FinishedRecipe
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.edamam.com/api/recipes/v2/"

// Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

// The Retrofit object with the Moshi converter.
private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

// A public interface that exposes the [edamamRecipe] method
interface EdamamApiService {
    /**
     * Returns a [Call] of [FinishedRecipe] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "." endpoint will be requested with the GET HTTP
     * method.
     */
    @GET(".")
    suspend fun edamamRecipe(
        @Query("type") type: String,
        @Query("beta") beta: Boolean,
        @Query("app_id") app_id: String,
        @Query("app_key") app_key: String,
        @Query("random") random: Boolean,
        @Query("q") q: String,
        @Query("diet") diet: Array<String>?,
        @Query("health") health: Array<String>?
    ): ca.neunition.data.model.api.FinishedRecipe
}

// A public Api object that exposes the lazy-initialized Retrofit service
object EdamamApi {
    val retrofitService: EdamamApiService by lazy { retrofit.create(EdamamApiService::class.java) }
}
