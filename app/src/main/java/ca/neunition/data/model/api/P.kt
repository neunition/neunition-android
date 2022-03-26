package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class P(
    @Json(name = "label")
    val label: String?,
    @Json(name = "quantity")
    val quantity: Double?,
    @Json(name = "unit")
    val unit: String?
)
