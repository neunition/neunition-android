package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Ingredient(
    @Json(name = "food")
    val food: String?,
    @Json(name = "foodCategory")
    val foodCategory: String?,
    @Json(name = "foodId")
    val foodId: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "measure")
    val measure: String?,
    @Json(name = "quantity")
    val quantity: Double?,
    @Json(name = "text")
    val text: String?,
    @Json(name = "weight")
    val weight: Double?
)
