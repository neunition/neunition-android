package ca.neunition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.neunition.data.model.api.FinishedRecipe
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [CarbonFoodCalc] and [Recipes].
 */
class EdamamViewModel : ViewModel() {
    private var edamamResultLiveData = MutableLiveData<ca.neunition.data.model.api.FinishedRecipe>()
    private var statusLiveData = MutableLiveData<String>()

    /**
     * Gets Edamam recipes from the Edamam API Retrofit service and updates the [FinishedRecipe]
     * [List] [LiveData].
     */
    fun getEdamamRecipes(
        type: String,
        beta: Boolean,
        app_id: String,
        app_key: String,
        random: Boolean,
        q: String,
        diet: Array<String>?,
        health: Array<String>?
    ) {
        edamamResultLiveData = MutableLiveData<ca.neunition.data.model.api.FinishedRecipe>()
        statusLiveData = MutableLiveData<String>()
        viewModelScope.launch {
            try {
                edamamResultLiveData.postValue(
                    EdamamApi.retrofitService.edamamRecipe(
                        type,
                        beta,
                        app_id,
                        app_key,
                        random,
                        q,
                        diet,
                        health
                    )
                )
            } catch (e: Exception) {
                statusLiveData.postValue("Oh no! There was a network error, please try again later.")
            }
        }
    }

    fun getEdamamResultLiveData(): MutableLiveData<ca.neunition.data.model.api.FinishedRecipe> {
        return edamamResultLiveData
    }

    fun getStatusLiveData(): MutableLiveData<String> {
        return statusLiveData
    }
}
