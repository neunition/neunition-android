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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.neunition.R
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.scoreColourChange
import ca.neunition.util.spannableFactory
import java.math.BigDecimal

class GreenhouseGasEmissionsFragment : Fragment() {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")

    private lateinit var todayScoreTextView: AppCompatTextView
    private lateinit var weekScoreTextView: AppCompatTextView
    private lateinit var monthScoreTextView: AppCompatTextView
    private lateinit var yearScoreTextView: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_greenhouse_gas_emissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayScoreTextView = view.findViewById(R.id.today_score_text_view)
        weekScoreTextView = view.findViewById(R.id.week_score_text_view)
        monthScoreTextView = view.findViewById(R.id.month_score_text_view)
        yearScoreTextView = view.findViewById(R.id.year_score_text_view)

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
            }
        }
    }
}
