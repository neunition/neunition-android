package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class RecipeSearchResults(
    @Json(name = "count")
    val count: Int?,
    @Json(name = "from")
    val from: Int?,
    @Json(name = "hits")
    val hits: List<Hit>?,
    @Json(name = "_links")
    val links: LinksX?,
    @Json(name = "to")
    val to: Int?
)
