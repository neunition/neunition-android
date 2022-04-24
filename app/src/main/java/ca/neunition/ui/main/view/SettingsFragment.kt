/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Settings screen to view/receive info about the app or change the behaviour of the app.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import ca.neunition.R
import ca.neunition.di.NotificationsClass
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var fullNamePreference: EditTextPreference
    private lateinit var userEmailPreference: Preference
    private lateinit var notificationsPreference: SwitchPreferenceCompat
    private lateinit var breakfastNotificationsPreference: SwitchPreferenceCompat
    private lateinit var lunchNotificationsPreference: SwitchPreferenceCompat
    private lateinit var dinnerNotificationsPreference: SwitchPreferenceCompat
    private lateinit var logOutPreference: Preference
    private lateinit var deleteAccountPreference: Preference

    private lateinit var loadingDialog: LoadingDialog

    @Inject lateinit var notificationsClass: NotificationsClass

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)

        fullNamePreference = findPreference("full_name_preference")!!
        userEmailPreference = findPreference("user_email_preference")!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationsPreference = findPreference("notifications_switch_preference")!!
            breakfastNotificationsPreference = findPreference("breakfast_notifications_switch_preference")!!
            lunchNotificationsPreference = findPreference("lunch_notifications_switch_preference")!!
            dinnerNotificationsPreference = findPreference("dinner_notifications_switch_preference")!!
            switchMainPrefIconTitle(sharedPreferences.getBoolean(notificationsPreference.key, false))
        }

        logOutPreference = findPreference("logout_preference")!!
        deleteAccountPreference = findPreference("delete_account_preference")!!

        loadingDialog = LoadingDialog(requireActivity())

        // Get the user's info from the Firebase Realtime Database
        firebaseDatabaseViewModel = ViewModelProvider(this)[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.getUsersLiveData().observe(viewLifecycleOwner) { users ->
            if (users != null) {
                fullNamePreference.text = users.fullName
            }
        }

        fullNamePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().trim().isEmpty()) {
                    Toast.makeText(requireActivity(), "Sorry, input cannot be empty.", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    firebaseDatabaseViewModel.updateChildValues("fullName", newValue.toString().trim())
                    true
                }
            }

        userEmailPreference.title = "Email: ${Constants.FIREBASE_AUTH.currentUser?.email}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationsPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val switched = newValue as? Boolean ?: false
                    switchMainPrefIconTitle(switched)
                    true
                }

            notificationsClass.createNotificationChannel()

            breakfastNotificationsPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val switched = newValue as? Boolean ?: false
                    notificationsClass.breakfastSwitchPref(switched)
                    true
                }

            lunchNotificationsPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val switched = newValue as? Boolean ?: false
                    notificationsClass.lunchSwitchPref(switched)
                    true
                }

            dinnerNotificationsPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val switched = newValue as? Boolean ?: false
                    notificationsClass.dinnerSwitchPref(switched)
                    true
                }
        }

        logOutPreference.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationsClass.breakfastCancelAlarm()
                    notificationsClass.lunchCancelAlarm()
                    notificationsClass.dinnerCancelAlarm()
                }
                Constants.FIREBASE_AUTH.signOut()
                GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
                true
            }

        deleteAccountPreference.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                // Display a dialog to warn the user about deleting their account
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Warning!")
                    .setMessage("Are you sure you want to delete your account? This can't be undone.")
                    .setCancelable(false)
                    .setPositiveButton("yes", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, i: Int) {
                            loadingDialog.startDialog()
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()
                            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso).silentSignIn()
                            googleSignInClient.addOnCompleteListener(object : OnCompleteListener<GoogleSignInAccount> {
                                override fun onComplete(task: Task<GoogleSignInAccount>) {
                                    try {
                                        // Google Sign In was successful, authenticate with Firebase
                                        val account = task.getResult(ApiException::class.java)
                                        deleteGoogleUser(account.idToken!!)
                                    } catch (e: ApiException) {
                                        loadingDialog.dismissDialog()
                                        Toast.makeText(
                                            requireActivity(),
                                            "Failed to delete account: ${e.status}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            })
                        }
                    })
                    .setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                builder.create().show()
                true
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun switchMainPrefIconTitle(switch: Boolean) {
        if (switch) {
            notificationsPreference.icon = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_notifications_active
            )
            notificationsPreference.title = "Receive Notifications"
            breakfastNotificationsPreference.isVisible = true
            lunchNotificationsPreference.isVisible = true
            dinnerNotificationsPreference.isVisible = true
        } else {
            notificationsPreference.icon = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_notifications_off
            )
            notificationsPreference.title = "Mute Notifications"
            breakfastNotificationsPreference.isVisible = false
            lunchNotificationsPreference.isVisible = false
            dinnerNotificationsPreference.isVisible = false
        }
        val breakfastSwitchOn = sharedPreferences.getBoolean(breakfastNotificationsPreference.key, true)
        val lunchSwitchOn = sharedPreferences.getBoolean(lunchNotificationsPreference.key, true)
        val dinnerSwitchOn = sharedPreferences.getBoolean(dinnerNotificationsPreference.key, true)
        notificationsClass.switchMainPref(switch, breakfastSwitchOn, lunchSwitchOn, dinnerSwitchOn)
    }

    internal fun deleteGoogleUser(freshGoogleIdToken: String) {
        val user = Constants.FIREBASE_AUTH.currentUser!!
        val credential = GoogleAuthProvider.getCredential(freshGoogleIdToken, null)
        user.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationsClass.breakfastCancelAlarm()
                        notificationsClass.lunchCancelAlarm()
                        notificationsClass.dinnerCancelAlarm()
                    }
                    sharedPreferences.edit().clear().apply()
                    GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                    Firebase.storage.getReference("/images/${user.uid}").delete()
                    firebaseDatabaseViewModel.removeUsersLiveData()
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingDialog.dismissDialog()
                                Toast.makeText(
                                    requireActivity(),
                                    "Your account and all of its data was successfully deleted.",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                loadingDialog.dismissDialog()
                                Toast.makeText(
                                    requireActivity(),
                                    "Failed to delete account: ${task.exception}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    loadingDialog.dismissDialog()
                    Toast.makeText(
                        requireActivity(),
                        "Failed to delete account: Unable to re-authenticate user. Please sign out and sign back in to delete your account.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
