/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Kotlin extension functions file for functions to be used be anywhere in the program.
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
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import ca.neunition.R
import ca.neunition.data.model.api.Ingredient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.BigDecimal

private val FOODS by lazy { Constants.MEALS }
private val SPECIAL_OILS: List<String> by lazy { listOf("palm", "soybean", "olive", "rapeseed", "sunflower") }

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

fun ingredientCardText(
    ingredientText: String,
    italicText: Boolean
): SpannableString {
    val ingredientTextSpannable = SpannableString(ingredientText)

    if (italicText) {
        ingredientTextSpannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            0,
            ingredientText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return ingredientTextSpannable
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

fun recipeCardScore(
    context: Context,
    score: BigDecimal
): SpannableString {
    val scoreSpannable = SpannableString(score.toString())

    if (score <= BigDecimal("1.08")) {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.greenScore)),
            0,
            score.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else if (score > BigDecimal("1.08") && score <= BigDecimal("1.61")) {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellowScore)),
            0,
            score.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else {
        scoreSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.redScore)),
            0,
            score.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return scoreSpannable
}

/**
 * Let the user know that it could not calculate the CO2 emissions for their food.
 *
 * @param msg the error message to be presented
 */
fun Fragment.noCalculations(msg: String) {
    // Display a dialog to the user that the app wasn't able to calculate their food's CO2 emissions
    val noCalcsBuilder = MaterialAlertDialogBuilder(this.requireActivity()).apply {
        setMessage(msg)
        setCancelable(false)
        setPositiveButton("ok") { dialog, _ ->
            dialog.dismiss()
        }
    }
    noCalcsBuilder.create().show()
    return
}

/**
 * Calculate the CO2 emissions for the ingredients received.
 *
 * @param ingredients the ingredients to calculate the carbon footprint for
 *
 * @return the user's CO2 emissions
 */
fun recipeCO2Analysis(ingredients: List<Ingredient>?): BigDecimal {
    var score = BigDecimal("0.00")

    if (ingredients != null) {
        for (ingredient in ingredients) {
            val re = Regex("[^%a-zA-Z0-9 ]")
            var foodName = "${ingredient.text?.lowercase()} ${ingredient.foodCategory?.lowercase()}"
            Log.d("testwfwefw", foodName)
            foodName = re.replace(foodName, " ")
            Log.d("testwfwefw", foodName)
            val mainFoods = foodName.split("\\s".toRegex())
            val foodWeight = ingredient.weight
            for (foodWord in mainFoods.indices) {
                if ((mainFoods[foodWord] in SPECIAL_OILS && foodWord + 1 < mainFoods.size && mainFoods[foodWord + 1] == "oil" && "${mainFoods[foodWord]} oil" in FOODS) || (mainFoods[foodWord] == "sunflower" && foodWord + 2 < mainFoods.size && "sunflower seed oil" in FOODS)) {
                    Log.d(
                        "testwfwefw",
                        "Food: ${mainFoods[foodWord]} || Weight: $foodWeight || CarbonWeight: ${FOODS["${mainFoods[foodWord]} oil"]!!} || IngrScore: ${
                            BigDecimal(FOODS["${mainFoods[foodWord]} oil"]!!.toString()).multiply(
                                BigDecimal(foodWeight.toString())
                            )
                        }"
                    )
                    score = score.add(
                        BigDecimal(FOODS["${mainFoods[foodWord]} oil"].toString()).multiply(
                            BigDecimal(foodWeight.toString())
                        )
                    )
                    Log.d("testwfwefw", "Score: $score")
                    break
                } else if (mainFoods[foodWord] in FOODS) {
                    if (mainFoods[foodWord] == "vegetable" && foodWord + 1 < mainFoods.size && mainFoods[foodWord + 1] == "oil") {
                        continue
                    } else {
                        Log.d(
                            "testwfwefw",
                            "Food: ${mainFoods[foodWord]} || Weight: $foodWeight || CarbonWeight: ${FOODS[mainFoods[foodWord]]!!} || IngrScore: ${
                                BigDecimal(FOODS[mainFoods[foodWord]]!!.toString()).multiply(
                                    BigDecimal(foodWeight.toString())
                                )
                            }"
                        )
                        score = score.add(
                            BigDecimal(FOODS[mainFoods[foodWord]].toString()).multiply(
                                BigDecimal(foodWeight.toString())
                            )
                        )
                        Log.d("testwfwefw", "Score: $score")
                        break
                    }
                }
            }
            Log.d(
                "testwfwefw",
                "-------------------------------------------------------------------------------------"
            )
        }
    }

    return score
}
