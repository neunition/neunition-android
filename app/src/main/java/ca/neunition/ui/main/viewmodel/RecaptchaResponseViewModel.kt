/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass the reCAPTCHA challenge into the View.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import android.app.Application
import ca.neunition.data.repository.RecaptchaRepository
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.AndroidViewModel
import ca.neunition.data.remote.response.RecaptchaVerifyResponse

class RecaptchaResponseViewModel(application: Application) : AndroidViewModel(application) {
    fun getmRecaptchaObservable(
        @NonNull baseUrl: String,
        @NonNull response: String,
        @NonNull key: String
    ) = RecaptchaRepository().doRecaptchaValidation(baseUrl, response, key)
}