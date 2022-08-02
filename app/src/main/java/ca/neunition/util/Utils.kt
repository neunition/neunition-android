/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Kotlin extension functions file for functions to be used be anywhere in the app.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.util

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ca.neunition.R
import java.math.BigDecimal

val spannableFactory = object : Spannable.Factory() {
    override fun newSpannable(source: CharSequence?): Spannable {
        return source as Spannable
    }
}

/**
 * Set the color of the status bar to match the background
 */
fun Activity.changeStatusBarColor() {
    val window = this.window
    val background = ResourcesCompat.getDrawable(this.resources, R.drawable.background_color, null)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)
    window.setBackgroundDrawable(background)
}

/**
 * Check if the user is connected to the internet.
 *
 * @param context the context of the application
 *
 * @return true if the user is connected to the internet, otherwise false
 */
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // for other device that are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // for checking internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}

fun toastErrorMessages(context: Context, noConnectionMessage: String, otherErrorMessage: String) {
    if (!isOnline(context)) {
        Toast.makeText(context, noConnectionMessage, Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, otherErrorMessage, Toast.LENGTH_LONG).show()
    }
}

/**
 * Hide the keyboard.
 *
 * @return whether the keyboard is showing or not
 */
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}

/**
 * Change color of the greenhouse gas emissions score based on the value received.
 * Color coding the scores: red, yellow, green. Yearly CO2: 2 tones (30% of that is food)
 *
 * @param score the GHG emissions
 *
 * @return a string with the new color for the text
 */
fun scoreColourChange(
    context: Context,
    period: String,
    score: BigDecimal,
    greenVal: String,
    yellowVal: String
): SpannableString {
    val scoreSpannable = SpannableString("$period $score kg of CO₂-eq").apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, period.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (score <= BigDecimal(greenVal)) {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.greenScore)),
            period.length + 1, // start
            scoreSpannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
    } else if (score > BigDecimal(greenVal) && score <= BigDecimal(yellowVal)) {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellowScore)),
            period.length + 1, // start
            scoreSpannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
    } else {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.redScore)),
            period.length + 1, // start
            scoreSpannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
    }

    return scoreSpannable
}
