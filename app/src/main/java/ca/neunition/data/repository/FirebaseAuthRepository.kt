/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data for Firebase Authentication.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.repository

import ca.neunition.ui.main.livedata.FirebaseAuthLiveData
import ca.neunition.util.Constants.FIREBASE_AUTH
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository {
    fun getFirebaseAuthState() = FirebaseAuthLiveData(FIREBASE_AUTH)
}
