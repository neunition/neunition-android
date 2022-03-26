/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass the Firebase Authentication data to the activity.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ca.neunition.data.repository.FirebaseAuthRepository

class FirebaseAuthViewModel : ViewModel() {
    private val repository = FirebaseAuthRepository()

    fun getAuthState(): LiveData<Boolean> {
        return repository.getFirebaseAuthState()
    }
}
