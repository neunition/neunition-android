/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data for Firebase Authentication.
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
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepository(private val application: Application) {
    /**
     * Proceed with the Firebase authentication process.
     *
     * @param credential a credential for the Firebase Authentication server to use to authenticate the user
     * @param provider the sign-in provider the user selected
     *
     * @return a observable User object to determine whether to save the User into the Realtime
     * Database or redirect them to MainActivity
     */
    fun firebaseAuthSignIn(
        credential: AuthCredential,
        provider: String
    ): MutableLiveData<User> {
        val authenticatedUserMutableLiveData = MutableLiveData<User>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Constants.FIREBASE_AUTH.signInWithCredential(credential).await().let {
                    val isNewUser = it.additionalUserInfo!!.isNewUser
                    val firebaseUser = Constants.FIREBASE_AUTH.currentUser

                    if (firebaseUser != null) {
                        var profileImageUrl = ""
                        if (provider == "Facebook") {
                            profileImageUrl =
                                Constants.FIREBASE_AUTH.currentUser?.photoUrl.toString() + "?type=large&width=720&height=720"
                        } else if (provider == "Google") {
                            profileImageUrl =
                                Constants.FIREBASE_AUTH.currentUser?.photoUrl.toString()
                                    .replace("s96-c", "s720-c")
                        }

                        val user = User(
                            firebaseUser.displayName,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            "",
                            profileImageUrl,
                            "",
                        )
                        user.isNew = isNewUser
                        withContext(Dispatchers.Main) {
                            authenticatedUserMutableLiveData.value = user
                        }
                    }
                }
            } catch (error: Exception) {
                withContext(Dispatchers.Main) {
                    toastErrorMessages(
                        application.applicationContext,
                        "Failed to sign in to your $provider account: No internet connection found. Please check your connection.",
                        "Failed to sign in to your $provider account: ${error.message}"
                    )
                }
            }
        }

        return authenticatedUserMutableLiveData
    }

    /**
     * Add the Firebase user to the Realtime Database if they don't already exist.
     *
     * @param authenticatedUser that data for creating a new user in the Realtime Database
     * @param provider the sign-in provider the user selected
     *
     * @return a observable User object that's created only if the User didn't exist before
     */
    fun createUserInDatabaseIfNotExists(
        authenticatedUser: User,
        provider: String,
    ): MutableLiveData<User> {
        val newUserMutableLiveData = MutableLiveData<User>()
        val userRef = Constants.FIREBASE_DATABASE.getReference("/users/${Constants.FIREBASE_AUTH.currentUser!!.uid}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRef.setValue(authenticatedUser).await().let {
                    authenticatedUser.isCreated = true
                    withContext(Dispatchers.Main) {
                        newUserMutableLiveData.value = authenticatedUser
                    }
                }
            } catch (error: Exception) {
                withContext(Dispatchers.Main) {
                    toastErrorMessages(
                        application.applicationContext,
                        "Failed to sign in to your $provider account: No internet connection found. Please check your connection.",
                        "Failed to sign in to your $provider account: ${error.message}"
                    )
                }
            }
        }

        return newUserMutableLiveData
    }
}
