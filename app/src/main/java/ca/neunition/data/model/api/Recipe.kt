package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Recipe(
    @Json(name = "image")
    val image: String?,
    @Json(name = "ingredients")
    val ingredients: List<Ingredient>?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "url")
    val url: String?
)
