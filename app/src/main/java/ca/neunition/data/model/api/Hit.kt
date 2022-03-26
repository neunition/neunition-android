package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Hit(
    @Json(name = "_links")
    val links: ca.neunition.data.model.api.Links?,
    @Json(name = "recipe")
    val recipe: ca.neunition.data.model.api.Recipe?
)
