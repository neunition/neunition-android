/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data for completing a reCAPTCHA challenge.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.repository

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.neunition.data.remote.response.RecaptchaVerifyResponse
import ca.neunition.data.remote.request.RecaptchaVerificationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RecaptchaRepository {
    fun doRecaptchaValidation(
        @NonNull baseUrl: String,
        @NonNull response: String,
        @NonNull key: String
    ): LiveData<RecaptchaVerifyResponse> {
        val data: MutableLiveData<RecaptchaVerifyResponse> = MutableLiveData()
        val params: MutableMap<String, String> = HashMap()

        params["secret"] = key
        params["response"] = response

        getRecaptchaValidationService(baseUrl).verifyResponse(params)
            .enqueue(object : Callback<RecaptchaVerifyResponse> {
                override fun onResponse(
                    call: Call<RecaptchaVerifyResponse>,
                    response: Response<RecaptchaVerifyResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<RecaptchaVerifyResponse>, t: Throwable) {
                    data.value = null
                }
            })

        return data
    }

    private fun getRecaptchaValidationService(
        @NonNull baseUrl: String
    ): RecaptchaVerificationService = getRetrofit(baseUrl).create(RecaptchaVerificationService::class.java)

    private fun getRetrofit(@NonNull baseUrl: String): Retrofit {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .client(client)
            .build()
    }
}
