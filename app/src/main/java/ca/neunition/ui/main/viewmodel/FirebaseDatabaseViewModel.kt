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
    /**
     * Get the latest user data from Firebase Realtime Database.
     *
     * @return Observable User object to for observing any changes to the Realtime Database and to
     * get those changes.
     */
    fun firebaseUserData(): LiveData<User?> {
        return databaseRepository.userDataFromFirebase()
    }

    /**
     * Set child node for user in Firebase Realtime Database.
     *
     * @param childVal The child node (key)
     * @param provider The new value to set
     */
    fun updateChildValue(childVal: String, newVal: String) {
        return databaseRepository.setNewChildValue(childVal, newVal)
    }

    /**
     * Set child node for user in Firebase Realtime Database.
     *
     * @param childVal The child node (key)
     * @param provider The new value to set
     */
    fun updateChildValue(childVal: String, newVal: Double) {
        return databaseRepository.setNewChildValue(childVal, newVal)
    }

    /**
     * Remove all user data from Firebase Realtime Database.
     */
    fun removeUser() {
        return databaseRepository.removeUserFromDatabase()
    }
}
