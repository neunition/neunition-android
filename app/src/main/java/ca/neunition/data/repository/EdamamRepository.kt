/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data for making a GET request to the Edamam Recipe Search API.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.repository

import androidx.lifecycle.LiveData
import ca.neunition.data.model.api.RecipeSearchResults
import ca.neunition.data.remote.request.RetrofitBuilder
import kotlinx.coroutines.*
import javax.inject.Singleton

@Singleton
object EdamamRepository {
    private var job: CompletableJob? = null
    private lateinit var recipeSearchResults: RecipeSearchResults

    /**
     * Call this method to make the API call
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
     * @return LiveData object of the JSON data returned from the API call
     */
    fun getRecipeSearchResults(
        type: String,
        beta: Boolean,
        app_id: String,
        app_key: String,
        q: String,
        random: Boolean,
        diet: Array<String>?,
        health: Array<String>?,
        field: Array<String>
    ): LiveData<RecipeSearchResults> {
        job = Job()
        return object : LiveData<RecipeSearchResults>() {
            override fun onActive() {
                super.onActive()
                job?.let { theJob ->
                    /* Create a unique coroutine on the background thread that's referencing this
                     * job. Cancel the job and it will cancel what's inside this CoroutineScope. */
                    CoroutineScope(Dispatchers.IO + theJob).launch {
                        try {
                            recipeSearchResults = RetrofitBuilder.edamamApiService.getEdamamRecipes(
                                type,
                                beta,
                                app_id,
                                app_key,
                                q,
                                random,
                                diet,
                                health,
                                field
                            )
                        } catch (e: Exception) {
                            recipeSearchResults = RecipeSearchResults(null, null, null, null, null)
                        } finally {
                            withContext(Dispatchers.Main) {
                                value = recipeSearchResults
                                theJob.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * If the fragment is destroyed, cancel the job in the ViewModel, then cancel in Repository and
     * everything will get cancelled. This will clean up resources and prevent network requests
     * pending in the background.
     */
    fun cancelJobs() {
        job?.cancel()
    }
}
