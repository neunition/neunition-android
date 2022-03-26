package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ingredient(
    @Json(name = "foodCategory")
    val foodCategory: String?,
    @Json(name = "foodId")
    val foodId: String?,
    @Json(name = "image")
    val image: Any?,
    @Json(name = "text")
    val text: String?,
    @Json(name = "weight")
    val weight: Double?
)
