/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * API service interface for making the GET request.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.request

import ca.neunition.data.model.api.RecipeSearchResults
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApiService {
    /**
     * GET request to Edamam's "api/recipes/v2/" endpoint.
     *
     * @param type Type of recipes to search for
     * @param beta Allow beta features in the request and response
     * @param app_id The application ID
     * @param app_key The application key
     * @param q Query text to be used to search for recipes
     * @param random Random selection of a maximum of 20 recipes based on the diet/health labels selected
     * @param diet Diet labels to select
     * @param health Health labels to select
     * @param field Recipe fields to be included in the API response
     *
     * @return The returned JSON response from the API call.
     */
    @GET("api/recipes/v2/")
    suspend fun getEdamamRecipes(
        @Query("type") type: String,
        @Query("beta") beta: Boolean,
        @Query("app_id") app_id: String,
        @Query("app_key") app_key: String,
        @Query("q") q: String,
        @Query("random") random: Boolean,
        @Query("diet") diet: Array<String>?,
        @Query("health") health: Array<String>?,
        @Query("field") field: Array<String>
    ): RecipeSearchResults
}
