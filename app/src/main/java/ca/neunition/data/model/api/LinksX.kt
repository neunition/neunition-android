package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinksX(
    @Json(name = "next")
    val next: ca.neunition.data.model.api.Next?
)
