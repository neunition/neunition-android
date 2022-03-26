/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Main screen for the user to interact with the app.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import ca.neunition.R
import ca.neunition.di.NotificationsClass
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.ViewPager2Adapter
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.Constants.FIREBASE_AUTH
import ca.neunition.util.changeStatusBarColor
import ca.neunition.util.isOnline
import ca.neunition.util.spannableFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel
    private lateinit var storage: FirebaseStorage

    // User's profile image
    private lateinit var profileImageUri: Uri
    private var fileSize: Float = 0.0f
    private var currentUrl = ""

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var fullNameTextView: AppCompatTextView
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var settingsImageView: AppCompatImageButton
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var noInternet: AppCompatTextView
    private lateinit var appBar: AppBarLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    private lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var notificationsClass: NotificationsClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeStatusBarColor()

        storage = Firebase.storage

        loadingDialog = LoadingDialog(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val mainNotificationSwitchOn = sharedPreferences.getBoolean("notifications_switch_preference", true)
            val breakfastSwitchOn = sharedPreferences.getBoolean("breakfast_notifications_switch_preference", true)
            val lunchSwitchOn = sharedPreferences.getBoolean("lunch_notifications_switch_preference", true)
            val dinnerSwitchOn = sharedPreferences.getBoolean("dinner_notifications_switch_preference", true)
            notificationsClass.switchMainPref(mainNotificationSwitchOn, breakfastSwitchOn, lunchSwitchOn, dinnerSwitchOn)
            notificationsClass.createNotificationChannel()
        }

        fullNameTextView = findViewById(R.id.user_full_name_text_view)
        profileImageView = findViewById(R.id.profile_image_view)
        settingsImageView = findViewById(R.id.settings_button)
        progressBar = findViewById(R.id.circular_progress_bar)
        noInternet = findViewById(R.id.no_internet_text_view)
        appBar = findViewById(R.id.app_bar_layout)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)

        fullNameTextView.setSpannableFactory(spannableFactory)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(fullNameTextView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)

        // Setup the app bar so the user can swipe or click on the tabs to go to different screens
        val pageAdapter = ViewPager2Adapter(this)
        viewPager2.apply {
            adapter = pageAdapter
            offscreenPageLimit = 2
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.first_tab_button)
                }
                1 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.second_tab_button)
                }
                else -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.third_tab_button)
                }
            }
        }.attach()

        // Check to see if the user is connected to the network
        if (!isOnline(applicationContext)) {
            progressBar.visibility = View.INVISIBLE
            noInternet.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.VISIBLE
            noInternet.visibility = View.INVISIBLE
        }

        // Obtain a new or prior instance of FirebaseDatabaseViewModel according to the lifecycle component from the ViewModelProvider class
        firebaseDatabaseViewModel = ViewModelProvider(this)[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.getUsersLiveData().observe(this) { users ->
            if (users != null) {
                fullNameTextView.setText(
                    SpannableString(users.fullName),
                    TextView.BufferType.SPANNABLE
                )

                CoroutineScope(Dispatchers.IO).launch {
                    Glide.get(applicationContext).clearDiskCache()
                }

                val imgUrl = users.profileImageUrl
                if (imgUrl != "" && currentUrl != imgUrl) {
                    currentUrl = imgUrl
                    // Min version 384px x 384px
                    Glide.with(applicationContext)
                        .load(currentUrl)
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(profileImageView)
                }

                profileImageView.visibility = View.VISIBLE
                fullNameTextView.visibility = View.VISIBLE
                settingsImageView.visibility = View.VISIBLE
                appBar.visibility = View.VISIBLE
                tabLayout.visibility = View.VISIBLE
                viewPager2.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                noInternet.visibility = View.INVISIBLE
            }
        }

        // Open the settings screen
        settingsImageView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val profileImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK && res.data != null) {
                // Proceed and check what the selected image was
                profileImageUri = res.data!!.data!!
                fileSize = getImageSize(applicationContext, profileImageUri)

                if (fileSize > 2.0) {
                    val builder = MaterialAlertDialogBuilder(this).apply {
                        setTitle("Image Too Large")
                        setMessage("The image you have selected exceeds the 2 MB limit.")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }
                    builder.create().show()
                } else {
                    // Upload the user's profile image to Firebase Storage
                    uploadProfileImage()
                }
            }
        }

        // Allow the user to change their profile image
        profileImageView.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Set Profile Photo")
                .setCancelable(false)
                .setPositiveButton("upload") { _, _ ->
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    profileImageLauncher.launch(intent)
                }
                .setNegativeButton("remove") { _, _ ->
                    Firebase.storage.getReference("/profile_pictures/${FIREBASE_AUTH.currentUser?.uid}")
                        .delete()
                    firebaseDatabaseViewModel.updateChildValues("profileImageUrl", "")
                    profileImageView.setImageResource(R.drawable.default_profile)
                }
                .setNeutralButton("cancel",
                    DialogInterface.OnClickListener { _, _ ->
                        return@OnClickListener
                    }
                )
            builder.create().show()
        }
    }

    /**
     * Get the size of the image the user is uploading.
     *
     * @param context the context of the application
     * @param uri the uri of the image
     *
     * @return the size of the image
     */
    private fun getImageSize(context: Context, uri: Uri): Float {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val imageSize: Float = cursor.getFloat(sizeIndex)
            cursor.close()
            return imageSize / (1024f * 1024f) // returns size in MB
        }
        return 0.0f
    }

    /**
     * Upload the user's profile image to Firebase Storage.
     */
    private fun uploadProfileImage() {
        loadingDialog.startDialog()
        val filename: String = FIREBASE_AUTH.currentUser?.uid ?: ""
        val ref: StorageReference = storage.getReference("/profile_pictures/$filename")
        ref.putFile(profileImageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    firebaseDatabaseViewModel.updateChildValues("profileImageUrl", it.toString())
                    loadingDialog.dismissDialog()
                }
            }
            .addOnFailureListener {
                loadingDialog.dismissDialog()
                if (!isOnline(applicationContext)) {
                    Toast.makeText(
                        this,
                        "Failed to upload image: No internet connection found. Please check your connection.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
