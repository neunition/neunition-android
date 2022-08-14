/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass the Firebase Authentication data to the repository.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ca.neunition.data.remote.response.User
import ca.neunition.data.repository.FirebaseAuthRepository
import com.google.firebase.auth.AuthCredential

class FirebaseAuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = FirebaseAuthRepository(application)

    /**
     * Call [firebaseAuthSignIn] method in [FirebaseAuthRepository].
     *
     * @param credential a credential for the Firebase Authentication server to use to authenticate the user
     * @param provider the sign-in provider the user selected
     */
    fun signInWithFirebase(credential: AuthCredential, provider: String): LiveData<User> {
        return authRepository.firebaseAuthSignIn(credential, provider)
    }
    
    /**
     * Call [createUserInDatabaseIfNotExists] method in [FirebaseAuthRepository].
     *
     * @param authenticatedUser that data for creating a new user in the Realtime Database
     * @param provider the sign-in provider the user selected
     */
    fun createUser(authenticatedUser: User, provider: String): LiveData<User> {
        return authRepository.createUserInDatabaseIfNotExists(authenticatedUser, provider)
    }

    fun cancelCoroutines() {
        authRepository.cancelCoroutines()
    }
}
