/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to check if the user is logged in or not using the repository.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ca.neunition.data.remote.response.User
import ca.neunition.data.repository.SplashRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val splashRepository = SplashRepository(application)

    /**
     * Call [checkIfUserIsAuthenticatedInFirebase] method in [SplashRepository].
     */
    fun checkIfUserIsAuthenticated(): LiveData<User> {
        return splashRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    /**
     * Call [addUserToLiveData] method in [SplashRepository].
     */
    fun getUserFromDatabase(): LiveData<User> {
        return splashRepository.addUserToLiveData()
    }
}
