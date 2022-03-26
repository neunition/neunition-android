/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewModel to pass on the Firebase Realtime Database data directly into the View.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ca.neunition.data.remote.response.Users
import ca.neunition.ui.main.livedata.FirebaseDatabaseLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabaseViewModel(
    private val UID: String = Firebase.auth.currentUser!!.uid,
    private val USERS_REF: DatabaseReference = Firebase.database.getReference("/users/$UID")
): ViewModel() {
    private val liveData: FirebaseDatabaseLiveData = FirebaseDatabaseLiveData(USERS_REF)
    private val usersLiveData: MediatorLiveData<Users?> = MediatorLiveData()

    init {
        usersLiveData.addSource(liveData) { dataSnapshot ->
            if (dataSnapshot != null) {
                usersLiveData.postValue(dataSnapshot.getValue(Users::class.java))
            } else {
                usersLiveData.value = null
            }
        }
    }

    fun getUsersLiveData() = usersLiveData

    fun updateChildValues(childVal: String, newVal: String) {
        USERS_REF.child(childVal).setValue(newVal)
    }

    fun updateChildValues(childVal: String, newVal: Double) {
        USERS_REF.child(childVal).setValue(newVal)
    }

    fun removeUsersLiveData() {
        USERS_REF.removeValue()
    }
}
