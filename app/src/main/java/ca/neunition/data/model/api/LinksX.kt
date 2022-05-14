package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class LinksX(
    @Json(name = "next")
    val next: Next?
)
