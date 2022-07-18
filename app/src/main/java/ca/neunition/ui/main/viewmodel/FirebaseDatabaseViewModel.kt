/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass the Firebase Realtime Database data to the repository.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ca.neunition.data.remote.response.User
import ca.neunition.data.repository.FirebaseDatabaseRepository

class FirebaseDatabaseViewModel(
    private val databaseRepository: FirebaseDatabaseRepository = FirebaseDatabaseRepository()
) : ViewModel() {

    fun firebaseUserData(): LiveData<User?> {
        return databaseRepository.userDataFromFirebase()
    }

    fun updateChildValue(childVal: String, newVal: String) {
        return databaseRepository.setNewChildValue(childVal, newVal)
    }

    fun updateChildValue(childVal: String, newVal: Double) {
        return databaseRepository.setNewChildValue(childVal, newVal)
    }

    fun removeUser() {
        return databaseRepository.removeUserFromDatabase()
    }
}
