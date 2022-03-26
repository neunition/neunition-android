/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * A splash screen with the company's logo will be shown to the user while the app is loading.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.neunition.R
import ca.neunition.ui.main.viewmodel.FirebaseAuthViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var firebaseAuthLiveData: Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        // Check to see if the user is already logged in.
        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
        firebaseAuthLiveData = firebaseAuthViewModel.getAuthState().observe(this) { isUserSignedOut ->
            val intent = when (isUserSignedOut) {
                true -> Intent(this, LoginActivity::class.java)
                false -> Intent(this, MainActivity::class.java)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finish()
            }, 100)
        }
    }
}
