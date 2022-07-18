package ca.neunition.data.repository

import androidx.lifecycle.MutableLiveData
import ca.neunition.data.remote.response.User
import ca.neunition.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseRepository(
    private val USER_REF: DatabaseReference = Constants.FIREBASE_DATABASE.getReference("/users/${Constants.FIREBASE_AUTH.currentUser!!.uid}")
) {
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

    fun setNewChildValue(childVal: String, newVal: String) {
        USER_REF.child(childVal).setValue(newVal)
    }

    fun setNewChildValue(childVal: String, newVal: Double) {
        USER_REF.child(childVal).setValue(newVal)
    }

    fun removeUserFromDatabase() {
        USER_REF.removeValue()
    }
}
