/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data from Firebase Authentication for the splash screen.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ca.neunition.data.remote.response.User
import ca.neunition.util.Constants
import ca.neunition.util.toastErrorMessages
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SplashRepository(
    private val application: Application,
    private val user: User = User(),
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Check if user is already logged into Firebase.
     *
     * @return Observable User object to determine if the user is logged in or not.
     */
    fun checkIfUserIsAuthenticatedInFirebase(): MutableLiveData<User> {
        val isUserAuthenticatedInFirebaseMutableLiveData = MutableLiveData<User>()

        if (Constants.FIREBASE_AUTH.currentUser == null) {
            user.isAuthenticated = false
            isUserAuthenticatedInFirebaseMutableLiveData.setValue(user)
        } else {
            user.isAuthenticated = true
            isUserAuthenticatedInFirebaseMutableLiveData.setValue(user)
        }

        return isUserAuthenticatedInFirebaseMutableLiveData
    }

    /**
     * Check if user is already logged into Firebase.
     *
     * @return Observable User object to get the user's data from the Realtime Database.
     */
    fun addUserToLiveData(): MutableLiveData<User> {
        val userMutableLiveData = MutableLiveData<User>()
        val userRef: DatabaseReference =
            Constants.FIREBASE_DATABASE.getReference("/users/${Constants.FIREBASE_AUTH.currentUser!!.uid}")

        scope.launch {
            try {
                userRef.get().await().let {
                    if (it.exists()) {
                        withContext(Dispatchers.Main) {
                            val user = it.getValue(User::class.java)!!
                            userMutableLiveData.value = user
                        }
                    }
                }
            } catch (error: Exception) {
                withContext(Dispatchers.Main) {
                    toastErrorMessages(
                        application.applicationContext,
                        "No internet connection found. Please check your connection.",
                        "${error.message}"
                    )
                }
            }
        }

        return userMutableLiveData
    }

    /**
     * Cancel all Firebase Authentication coroutine scopes.
     */
    fun cancelCoroutines() {
        scope.cancel()
    }
}
