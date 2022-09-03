/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Expose and centralize the data for Firebase Realtime Database.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.repository

import androidx.lifecycle.MutableLiveData
import ca.neunition.data.remote.response.User
import ca.neunition.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseRepository(
    private val USER_REF: DatabaseReference = Constants.FIREBASE_DATABASE.getReference(
        "/users/${Constants.FIREBASE_AUTH.currentUser!!.uid}"
    )
) {
    /**
     * Get the latest user data from Firebase Realtime Database.
     *
     * @return Observable User object to for observing any changes to the Realtime Database and to
     * get those changes.
     */
    fun userDataFromFirebase(): MutableLiveData<User?> {
        val userMutableLiveData = MutableLiveData<User?>()

        USER_REF.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                userMutableLiveData.postValue(userData)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return userMutableLiveData
    }

    /**
     * Set child node for user in Firebase Realtime Database.
     *
     * @param childVal The child node (key)
     * @param provider The new value to set
     */
    fun setNewChildValue(childVal: String, newVal: String) {
        USER_REF.child(childVal).setValue(newVal)
    }

    /**
     * Set child node for user in Firebase Realtime Database.
     *
     * @param childVal The child node (key)
     * @param provider The new value to set
     */
    fun setNewChildValue(childVal: String, newVal: Double) {
        USER_REF.child(childVal).setValue(newVal)
    }

    /**
     * Remove all user data from Firebase Realtime Database.
     */
    fun removeUserFromDatabase() {
        USER_REF.removeValue()
    }
}
