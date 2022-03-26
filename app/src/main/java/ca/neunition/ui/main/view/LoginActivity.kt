/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Ask the user to login with either their Google account or Facebook account.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.CompoundButtonCompat
import ca.neunition.R
import ca.neunition.data.remote.response.Users
import ca.neunition.util.Constants.FIREBASE_AUTH
import ca.neunition.util.changeStatusBarColor
import ca.neunition.util.isOnline
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInButton: AppCompatButton
    private lateinit var facebookSignInButton: AppCompatButton
    private lateinit var agreeCheckBox: AppCompatCheckBox
    private lateinit var termsPrivacyAgreement: AppCompatTextView

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // private lateinit var recaptchaViewModel: RecaptchaResponseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeStatusBarColor()

        googleSignInButton = findViewById(R.id.google_sign_in_button)
        facebookSignInButton = findViewById(R.id.facebook_sign_in_button)

        agreeCheckBox = findViewById(R.id.agree_check_box)
        CompoundButtonCompat.setButtonTintList(agreeCheckBox, ColorStateList.valueOf(Color.WHITE))

        termsPrivacyAgreement = findViewById(R.id.terms_privacy_agreement)
        termsPrivacyAgreement.makeLinks(
            Pair("Terms & Conditions", View.OnClickListener {
                // link to terms & conditions
            }),
            Pair("Privacy Policy", View.OnClickListener {
                // link to privacy policy
            })
        )

        // recaptchaViewModel = ViewModelProvider(this)[RecaptchaResponseViewModel::class.java]

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
                if (res.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account.idToken!!)
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
            if (!agreeCheckBox.isChecked) {
                Toast.makeText(
                    this,
                    "You must accept the Terms & Conditions and Privacy Policy to continue using our app.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
                /*SafetyNet.getClient(this).verifyWithRecaptcha(BuildConfig.RECAPTCHA_SITE_KEY)
                    .addOnSuccessListener(RecaptchaSuccessListener())
                    .addOnFailureListener(RecaptchaFailureListener())*/
            }
        }

        facebookSignInButton.setOnClickListener {
            // When we create the facebook page for Neunition
            true
        }
    }

    /**
     * Authenticate Google sign in with Firebase
     *
     * @param idToken An ID token from the GoogleSignInAccount object that gets exchanged for a
     * Firebase credential
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FIREBASE_AUTH.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (task.result.additionalUserInfo?.isNewUser == true) {
                        // User data to save on Firebase Realtime Database
                        val uid = FIREBASE_AUTH.uid ?: ""
                        val ref = Firebase.database.getReference("/users/$uid")
                        val newUser =
                            Users(
                                FIREBASE_AUTH.currentUser?.displayName,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                "",
                                "",
                                "",
                            )
                        ref.setValue(newUser)
                    }

                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            // If sign in fails, display a message to the user.
            .addOnFailureListener {
                if (!isOnline(applicationContext)) {
                    Toast.makeText(
                        this,
                        "Failed to sign in to your Google account: No internet connection found. Please check your connection.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to sign in to your Google account: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    /*private inner class RecaptchaSuccessListener : OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse> {
        override fun onSuccess(recaptchaTokenResponse: SafetyNetApi.RecaptchaTokenResponse) {
            val userResponseToken = recaptchaTokenResponse.tokenResult
            if (userResponseToken != null && userResponseToken.isNotEmpty()) {
                recaptchaViewModel.getmRecaptchaObservable("https://www.google.com", userResponseToken, BuildConfig.RECAPTCHA_SECRET_KEY).observe(this@LoginActivity, object : Observer<RecaptchaVerifyResponse> {
                    override fun onChanged(@Nullable recaptchaVerifyResponse: RecaptchaVerifyResponse?) {
                        if (recaptchaVerifyResponse != null && recaptchaVerifyResponse.success) {

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Failed to verify reCAPTCHA: ${recaptchaVerifyResponse?.error_codes}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
            } else {
                Toast.makeText(
                    applicationContext,
                    "Failed to verify reCAPTCHA",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class RecaptchaFailureListener : OnFailureListener {
        override fun onFailure(@NonNull e: Exception) {
            Toast.makeText(
                applicationContext,
                "Failed to verify reCAPTCHA: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }*/
}
