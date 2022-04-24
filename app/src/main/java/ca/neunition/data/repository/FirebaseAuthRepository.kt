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
import ca.neunition.util.Constants

class FirebaseAuthRepository {
    fun getFirebaseAuthState() = FirebaseAuthLiveData(Constants.FIREBASE_AUTH)
}
