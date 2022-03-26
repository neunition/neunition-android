/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Show the user's food GHG emissions for the current day, current week, current month, and current
 * year.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.neunition.EdamamViewModel
import ca.neunition.R
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class GreenhouseGasEmissionsFragment : Fragment() {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    // DEPRECATED
    private lateinit var edamamViewModel: EdamamViewModel

    // GHG scores
    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")

    // DEPRECATED
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var todayScoreTextView: AppCompatTextView
    private lateinit var weekScoreTextView: AppCompatTextView
    private lateinit var monthScoreTextView: AppCompatTextView
    private lateinit var yearScoreTextView: AppCompatTextView
    private lateinit var carbonMealCalc: ShapeableImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carbon_food_calc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DEPRECATED
        loadingDialog = LoadingDialog(requireActivity())

        todayScoreTextView = view.findViewById(R.id.today_score_text_view)
        weekScoreTextView = view.findViewById(R.id.week_score_text_view)
        monthScoreTextView = view.findViewById(R.id.month_score_text_view)
        yearScoreTextView = view.findViewById(R.id.year_score_text_view)
        // DEPRECATED
        carbonMealCalc = view.findViewById(R.id.carbon_food_calc_button)

        todayScoreTextView.setSpannableFactory(spannableFactory)
        weekScoreTextView.setSpannableFactory(spannableFactory)
        monthScoreTextView.setSpannableFactory(spannableFactory)
        yearScoreTextView.setSpannableFactory(spannableFactory)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(todayScoreTextView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(weekScoreTextView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(monthScoreTextView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(yearScoreTextView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)

        // Get the user's info from the Firebase Realtime Database
        firebaseDatabaseViewModel = ViewModelProvider(this)[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.getUsersLiveData().observe(viewLifecycleOwner) { users ->
            if (users != null) {
                dailyScore = BigDecimal(users.daily.toString())
                todayScoreTextView.setText(
                    scoreColourChange(
                        requireActivity(),
                        "Today:",
                        dailyScore,
                        "1.08",
                        "1.61"
                    ), TextView.BufferType.SPANNABLE
                )

                weeklyScore = BigDecimal(users.weekly.toString())
                weekScoreTextView.setText(
                    scoreColourChange(
                        requireActivity(),
                        "This Week:",
                        weeklyScore,
                        "7.56",
                        "11.27"
                    ), TextView.BufferType.SPANNABLE
                )

                monthlyScore = BigDecimal(users.monthly.toString())
                monthScoreTextView.setText(
                    scoreColourChange(
                        requireActivity(),
                        "This Month:",
                        monthlyScore,
                        "33.48",
                        "49.91"
                    ), TextView.BufferType.SPANNABLE
                )

                yearlyScore = BigDecimal(users.yearly.toString())
                yearScoreTextView.setText(
                    scoreColourChange(
                        requireActivity(),
                        "This Year:",
                        yearlyScore,
                        "394.20",
                        "587.65"
                    ), TextView.BufferType.SPANNABLE
                )

                todayScoreTextView.visibility = View.VISIBLE
                weekScoreTextView.visibility = View.VISIBLE
                monthScoreTextView.visibility = View.VISIBLE
                yearScoreTextView.visibility = View.VISIBLE
                carbonMealCalc.visibility = View.VISIBLE
            }
        }

        /***************************** DEPRECATED **********************************************/
        // Allow the user to enter the name of their meal
        carbonMealCalc.setOnClickListener {
            val edamamBuilder = MaterialAlertDialogBuilder(requireContext())
            edamamBuilder.setMessage("Enter the name of your food/drink:")

            val input = EditText(activity)
            edamamBuilder.setView(input)
            edamamBuilder.setCancelable(false)
            edamamBuilder.setPositiveButton("submit") { _, _ ->
                loadingDialog.startDialog()
                val meal = input.text.toString().capitalizeWords().trim()
                edamamViewModel = ViewModelProvider(this).get(EdamamViewModel::class.java)
                edamamViewModel.getEdamamRecipes(
                    "public",
                    false,
                    Constants.EDAMAM_API_ID,
                    Constants.EDAMAM_API_KEY,
                    false,
                    meal,
                    null,
                    null
                )
                edamamViewModel.getEdamamResultLiveData().observe(viewLifecycleOwner) {
                    if (it.hits!!.isEmpty()) {
                        loadingDialog.dismissDialog()
                        noCalculations("Sorry, we could not calculate the CO₂-eq of your food/drink.")
                    } else {
                        // Calculate GHG emissions for the user's food
                        val score: BigDecimal = recipeCO2Analysis(
                            it.hits[0].recipe?.ingredients
                        ).setScale(2, RoundingMode.HALF_UP)
                        showFinalCalc(meal, score)
                    }
                }
                edamamViewModel.getStatusLiveData().observe(viewLifecycleOwner) {
                    loadingDialog.dismissDialog()
                    noCalculations(it)
                }
            }

            edamamBuilder.setNegativeButton(
                "cancel",
                DialogInterface.OnClickListener { _, _ -> return@OnClickListener }
            )

            val edamamDialog = edamamBuilder.create()
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    edamamDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().trim().isNotEmpty()
                }
            })
            edamamDialog.show()
            edamamDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        /***************************** DEPRECATED **********************************************/
    }

    /**
     * DEPRECATED
     *
     * Convert the first letter of every word in a string to uppercase and make the rest of the
     * letters lowercase.
     */
    private fun String.capitalizeWords(): String =
        lowercase().split(" ").joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }

    /**
     * DEPRECATED
     *
     * Display a dialog to show the name of the food and the estimated GHG emissions score for the
     * food.
     *
     * @param meal the name of the food
     * @param score the GHG emissions score for the food
     */
    private fun showFinalCalc(meal: String, score: BigDecimal) {
        loadingDialog.dismissDialog()
        val mealConfirmBuilder = MaterialAlertDialogBuilder(requireContext())
        mealConfirmBuilder.setCancelable(false)
        mealConfirmBuilder.setMessage("Food: $meal\nGHG Emissions: $score kg of CO₂-eq")
            .setPositiveButton(
                "submit"
            ) { _, _ ->
                if (isOnline(requireActivity().applicationContext)) {
                    dailyScore = dailyScore.add(score)
                    weeklyScore = weeklyScore.add(score)
                    monthlyScore = monthlyScore.add(score)
                    yearlyScore = yearlyScore.add(score)
                    firebaseDatabaseViewModel.updateChildValues("daily", dailyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValues("weekly", weeklyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValues("monthly", monthlyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValues("yearly", yearlyScore.toDouble())
                } else {
                    showFinalCalc(meal, score)
                    Toast.makeText(
                        requireActivity(),
                        "No internet connection found. Please check your connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(
                "cancel",
                DialogInterface.OnClickListener { _, _ -> return@OnClickListener }
            )
        mealConfirmBuilder.show()
    }
}
