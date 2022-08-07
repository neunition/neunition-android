/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass the JSON object from the Edamam Recipe Search API to the fragment.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ca.neunition.BuildConfig
import ca.neunition.data.model.api.RecipeSearchResults
import ca.neunition.data.repository.EdamamRepository

class EdamamViewModel : ViewModel() {
    private val _q = MutableLiveData<String>()
    private var _random = false
    private var _diet: Array<String>? = null
    private var _health: Array<String>? = null

    // Observe the MutaleLiveData object for any changes. Map it from a String to a LiveData<RecipeSearchResults> object
    val recipeSearchResults: LiveData<RecipeSearchResults> = Transformations.switchMap(_q) {
        EdamamRepository.getRecipeSearchResults(
            "public",
            false,
            EDAMAM_API_ID,
            EDAMAM_API_KEY,
            it.toString(),
            _random,
            _diet,
            _health,
            arrayOf("label", "image", "url", "ingredients")
        )
    }

    /**
     * Set the values to be used for making the API call.
     *
     * @param q The user input
     * @param random Whether to get a list of random recipes based off of the user's selected diet/health labels
     * @param diet The selected diet labels
     * @param health The selected health labels
     */
    fun setQueries(q: String, random: Boolean, diet: Array<String>?, health: Array<String>?) {
        _random = random
        _diet = diet
        _health = health

        // Trigger switchMap operator if LiveData object changes to call the API
        _q.value = q
    }

    /**
     * Cancel any jobs running from the repository.
     */
    fun cancelJobs() {
        EdamamRepository.cancelJobs()
    }

    companion object {
        private val EDAMAM_API_ID: String by lazy { BuildConfig.EDAMAM_API_ID }
        private val EDAMAM_API_KEY: String by lazy { BuildConfig.EDAMAM_API_KEY }
    }
}
