/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Observe changes to the Firebase Realtime Database.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.livedata

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class FirebaseDatabaseLiveData(ref: DatabaseReference) : LiveData<DataSnapshot>() {
    private val query: Query = ref
    private var listenerRemovePending = false
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val listener: MyFirebaseDatabaseValueEventListener = MyFirebaseDatabaseValueEventListener()

    private val removeListener = Runnable {
        query.removeEventListener(listener)
        listenerRemovePending = false
    }

    override fun onActive() {
        super.onActive()
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            query.addValueEventListener(listener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        super.onInactive()
        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }

    // Listener gets triggered when new data changes are detected (come in the form of a DataSnapshot)
    private inner class MyFirebaseDatabaseValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            value = snapshot
        }

        override fun onCancelled(error: DatabaseError) {
            return
        }
    }
}
