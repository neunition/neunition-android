package ca.neunition.data.model.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Self(
    @Json(name = "href")
    val href: String?,
    @Json(name = "title")
    val title: String?
)
