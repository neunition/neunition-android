package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Recipe(
    @Json(name = "calories")
    val calories: Double?,
    @Json(name = "cautions")
    val cautions: List<String>?,
    @Json(name = "cuisineType")
    val cuisineType: List<String>?,
    @Json(name = "dietLabels")
    val dietLabels: List<String>?,
    @Json(name = "digest")
    val digest: List<ca.neunition.data.model.api.Digest>?,
    @Json(name = "dishType")
    val dishType: List<String>?,
    @Json(name = "healthLabels")
    val healthLabels: List<String>?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "ingredientLines")
    val ingredientLines: List<String>?,
    @Json(name = "ingredients")
    val ingredients: List<ca.neunition.data.model.api.Ingredient>?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "mealType")
    val mealType: List<String>?,
    @Json(name = "shareAs")
    val shareAs: String?,
    @Json(name = "source")
    val source: String?,
    @Json(name = "totalDaily")
    val totalDaily: ca.neunition.data.model.api.TotalDaily?,
    @Json(name = "totalNutrients")
    val totalNutrients: ca.neunition.data.model.api.TotalNutrients?,
    @Json(name = "totalTime")
    val totalTime: Double?,
    @Json(name = "totalWeight")
    val totalWeight: Double?,
    @Json(name = "uri")
    val uri: String?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "yieldx")
    val yieldx: Double?
)
