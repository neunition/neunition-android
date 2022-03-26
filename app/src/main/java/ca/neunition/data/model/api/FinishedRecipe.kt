package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinishedRecipe(
    @Json(name = "count")
    val count: Int?,
    @Json(name = "from")
    val from: Int?,
    @Json(name = "hits")
    val hits: List<ca.neunition.data.model.api.Hit>?,
    @Json(name = "_links")
    val links: ca.neunition.data.model.api.LinksX?,
    @Json(name = "to")
    val to: Int?
)
