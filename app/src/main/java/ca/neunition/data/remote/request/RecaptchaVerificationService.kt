/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * POST request verify a user's response to a reCAPTCHA challenge.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.request

import ca.neunition.data.remote.response.RecaptchaVerifyResponse
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface RecaptchaVerificationService {
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("/recaptcha/api/siteverify")
    fun verifyResponse(@QueryMap params: Map<String, String>): Call<RecaptchaVerifyResponse>
}