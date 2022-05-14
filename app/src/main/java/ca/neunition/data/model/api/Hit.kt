package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Hit(
    @Json(name = "_links")
    val links: Links?,
    @Json(name = "recipe")
    val recipe: Recipe?
)
