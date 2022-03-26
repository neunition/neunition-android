/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Deserialize the JSON response from the reCAPTCHA challenge.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecaptchaVerifyResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "challenge_ts")
    val challenge_ts: String,
    @Json(name = "apk_package_name")
    val apk_package_name: String,
    @Json(name = "error-codes")
    val error_codes: List<String>? = null
)
