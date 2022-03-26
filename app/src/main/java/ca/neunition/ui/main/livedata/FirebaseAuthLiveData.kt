/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Observe if the current FirebaseUser is logged in or not.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthLiveData(private val auth: FirebaseAuth): LiveData<Boolean>(), FirebaseAuth.AuthStateListener {
    /**
     * Set the value of the FirebaseUserLiveData object to the value of the current FirebaseUser.
     *
     * @param auth A FirebaseAuth instance for getting the current user
     *
     * @return null if the user is not logged in, otherwise the currently signed in FirebaseUser
     */
    override fun onAuthStateChanged(auth: FirebaseAuth) {
        value = auth.currentUser == null
    }

    /**
     * When this FirebaseUserLiveData object has an active observer, start observing the
     * FirebaseAuth state to see if there is currently a logged in user.
     */
    override fun onActive() {
        super.onActive()
        auth.addAuthStateListener(this)
    }

    /**
     * When this FirebaseUserLiveData object no longer has an active observer, stop observing the
     * FirebaseAuth state to prevent memory leaks.
     */
    override fun onInactive() {
        super.onInactive()
        auth.removeAuthStateListener(this)
    }
}
