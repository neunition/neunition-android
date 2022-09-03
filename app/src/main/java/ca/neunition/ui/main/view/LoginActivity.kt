/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Ask the user to sign in with either their Facebook account or Google account.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ca.neunition.R
import ca.neunition.ui.main.viewmodel.FirebaseAuthViewModel
import ca.neunition.util.Constants
import ca.neunition.util.changeStatusBarColor
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel

    private lateinit var facebookSignInButton: AppCompatButton
    private lateinit var googleSignInButton: AppCompatButton

    private lateinit var termsPrivacyAgreement: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeStatusBarColor()

        facebookSignInButton = findViewById(R.id.facebook_sign_in_button)
        googleSignInButton = findViewById(R.id.google_sign_in_button)

        termsPrivacyAgreement = findViewById(R.id.terms_privacy_agreement)
        termsPrivacyAgreement.makeLinks(
            Pair("Terms & Conditions", View.OnClickListener {
                Constants.CUSTOM_TABS_BUILDER.launchUrl(this, Uri.parse("https://www.google.com/"))
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Constants.CUSTOM_TABS_BUILDER.launchUrl(this, Uri.parse("https://www.cnn.com/"))
            })
        )

        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]

        /****************************** Facebook Login ********************************************/
        val callbackManager = CallbackManager.Factory.create()

        facebookSignInButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                callbackManager,
                arrayOf("email", "public_profile").toList()
            )
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        val credential =
                            FacebookAuthProvider.getCredential(result.accessToken.token)
                        signInWithFirebaseAuthCredential(credential, "Facebook")
                    }

                    override fun onCancel() {
                        Toast.makeText(
                            this@LoginActivity,
                            "Facebook Login Cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(
                            this@LoginActivity,
                            "${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
        /****************************** Facebook Login ********************************************/

        /******************************* Google Login *********************************************/
        val oneTapClient = Identity.getSignInClient(this)
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        val googleSignInLauncher =
            registerForActivityResult(StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    try {
                        val googleCredential =
                            oneTapClient.getSignInCredentialFromIntent(result.data)
                        val idToken = googleCredential.googleIdToken
                        when {
                            idToken != null -> {
                                // Got an ID token from Google. Use it to authenticate with Firebase.
                                val credential = GoogleAuthProvider.getCredential(idToken, null)
                                signInWithFirebaseAuthCredential(credential, "Google")
                            }
                            else -> {
                                Toast.makeText(
                                    this,
                                    "Google sign in failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(
                            this,
                            "Google sign in failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        googleSignInButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    oneTapClient.beginSignIn(signInRequest).await().let { result ->
                        googleSignInLauncher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                .build()
                        )
                    }
                } catch (error: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            error.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        /******************************* Google Login *********************************************/
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuthViewModel.cancelCoroutines()
    }

    /**
     * Using Facebook or Google credentials, sign into Firebase. Set credentials in our
     * AuthViewModel and then start observing the changes.
     *
     * @param credential a credential for the Firebase Authentication server to use to authenticate the user
     * @param provider the sign-in provider the user selected
     */
    private fun signInWithFirebaseAuthCredential(credential: AuthCredential, provider: String) {
        // First observer for signing the user into Firebase
        firebaseAuthViewModel.signInWithFirebase(credential, provider)
            .observe(this) { authenticatedUser ->
                if (authenticatedUser.isNew) {
                    // Second observer (optional) for creating a new user in the Realtime Database
                    firebaseAuthViewModel.createUser(authenticatedUser, provider).observe(this) {
                        goToMainActivity()
                    }
                } else {
                    goToMainActivity()
                }
            }
    }

    /**
     * Go to the main screen after the user successfully signs in.
     */
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Make multiple texts in a string clickable.
     *
     * @param links the specific text that should be clickable
     */
    private fun AppCompatTextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.color = Color.WHITE
                    textPaint.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}
