/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * A splash screen with the company's logo will be shown to the user while the app is loading.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.neunition.R
import ca.neunition.ui.main.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.checkIfUserIsAuthenticated().observe(this) { user ->
            if (!user.isAuthenticated) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                splashViewModel.getUserFromDatabase().observe(this) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
