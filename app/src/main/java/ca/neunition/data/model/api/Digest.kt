package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Digest(
    @Json(name = "daily")
    val daily: Double?,
    @Json(name = "hasRDI")
    val hasRDI: Boolean?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "schemaOrgTag")
    val schemaOrgTag: String?,
    @Json(name = "sub")
    val sub: List<ca.neunition.data.model.api.Sub>?,
    @Json(name = "tag")
    val tag: String?,
    @Json(name = "total")
    val total: Double?,
    @Json(name = "unit")
    val unit: String?
)
