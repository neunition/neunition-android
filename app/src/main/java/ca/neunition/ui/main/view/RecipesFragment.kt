/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Allow the user to search for recipes that will also shows their respective GHG emissions. User
 * can add GHG emissions of a specific recipe to their current day, current week, current month,
 * and current year scores.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.BigDecimalAdapter
import ca.neunition.ui.main.adapter.RecipeCardAdapter
import ca.neunition.ui.main.viewmodel.EdamamViewModel
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL

class RecipesFragment : Fragment(), RecipeCardAdapter.OnClickListener {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel
    private lateinit var edamamViewModel: EdamamViewModel

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var searchRecipeEditTextView: AppCompatEditText
    private lateinit var searchRecipeButton: AppCompatImageButton
    private lateinit var randomRecipeButton: AppCompatImageButton

    private var dietLabelUrl = arrayOf<String>()
    private var healthLabelUrl = arrayOf<String>()

    private lateinit var labelsTextView: AppCompatTextView
    private var labelsList = arrayListOf<Int>()
    private lateinit var checkedLabels: StringBuilder
    private var selectedLabels = BooleanArray(Constants.LABELS.size)

    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")

    private var currentJSONObject = ""
    private lateinit var recipesRecyclerView: RecyclerView
    private var recipesList = ArrayList<RecipeCard>()
    private lateinit var animationController: LayoutAnimationController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        searchRecipeEditTextView = view.findViewById(R.id.search_recipe_edit_text_view)
        searchRecipeButton = view.findViewById(R.id.search_recipe_button)
        randomRecipeButton = view.findViewById(R.id.random_recipe_button)
        labelsTextView = view.findViewById(R.id.select_label_text_view)
        recipesRecyclerView = view.findViewById(R.id.edamam_recipes_recycler_view)

        animationController = AnimationUtils.loadLayoutAnimation(requireActivity(), R.anim.recipes_recycler_view_animation)

        firebaseDatabaseViewModel = ViewModelProvider(requireActivity())[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.firebaseUserData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dailyScore = BigDecimal(user.daily.toString())
                weeklyScore = BigDecimal(user.weekly.toString())
                monthlyScore = BigDecimal(user.monthly.toString())
                yearlyScore = BigDecimal(user.yearly.toString())
                if (currentJSONObject != user.recipesJsonData) {
                    if (user.recipesJsonData != "") {
                        recipesList = jsonAdapter.fromJson(user.recipesJsonData)!!
                        recipesList = recipesList.sortedBy { it.recipeScore }.toCollection(ArrayList())
                        verifyJsonData()
                    }
                    initRecyclerView()
                    currentJSONObject = user.recipesJsonData
                } else if (user.recipesJsonData == "") {
                    initRecyclerView()
                }
            }
        }

        edamamViewModel = ViewModelProvider(requireActivity())[EdamamViewModel::class.java]
        edamamViewModel.recipeSearchResults.observe(viewLifecycleOwner) {
            when {
                it.hits == null -> {
                    loadingDialog.dismissDialog()
                    noCalculations("Error", "Oops, something went wrong. Please try again later.")
                }
                it.hits.isEmpty() -> {
                    loadingDialog.dismissDialog()
                    noCalculations("", "Sorry, we couldn't find any recipes for your search.")
                }
                else -> {
                    for (i in it.hits.indices) {
                        val score = recipeCO2Analysis(it.hits[i].recipe?.ingredients).setScale(
                            2,
                            RoundingMode.HALF_UP
                        )

                        val item = RecipeCard(
                            it.hits[i].recipe?.image,
                            it.hits[i].recipe?.label,
                            score,
                            it.hits[i].recipe?.url
                        )

                        recipesList += item
                    }

                    firebaseDatabaseViewModel.updateChildValue("recipesJsonData", jsonAdapter.toJson(recipesList))

                    loadingDialog.dismissDialog()
                }
            }
        }

        labelsTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.apply {
                    setTitle("Select diet/health labels")
                    setCancelable(false)
                    setMultiChoiceItems(Constants.LABELS, selectedLabels) { _, i, b ->
                        // When checkbox is selected
                        if (b) {
                            // Add position to labels list
                            labelsList.apply {
                                add(i)
                                sort()
                            }
                        } else {
                            // When checkbox is unselected remove position from labels list
                            labelsList.remove(i)
                        }
                    }
                    setPositiveButton("ok", DialogInterface.OnClickListener { _, _ ->
                        checkedLabels = StringBuilder()

                        val dietLabelsList = arrayListOf<String>()
                        val healthLabelsList = arrayListOf<String>()

                        for (j in 0 until labelsList.size) {
                            var choice = Constants.LABELS[labelsList[j]]

                            checkedLabels.append(choice)
                            if (j != labelsList.size - 1) {
                                checkedLabels.append(", ")
                            }

                            choice = choice.lowercase()
                            if (choice in Constants.DIET_PARAMETERS) {
                                dietLabelsList.add(choice)
                            } else {
                                healthLabelsList.add(choice)
                            }
                        }

                        labelsTextView.text = checkedLabels.toString()
                        dietLabelUrl = dietLabelsList.toTypedArray()
                        healthLabelUrl = healthLabelsList.toTypedArray()

                        return@OnClickListener
                    })
                    setNegativeButton("clear", DialogInterface.OnClickListener { _, _ ->
                        for (j in selectedLabels.indices) {
                            selectedLabels[j] = false
                        }

                        labelsList.clear()
                        checkedLabels = StringBuilder()
                        labelsTextView.text = ""
                        dietLabelUrl = arrayOf()
                        healthLabelUrl = arrayOf()

                        return@OnClickListener
                    })
                    show()
                }
            }
        })

        searchRecipeButton.setOnClickListener {
            val recipeName = searchRecipeEditTextView.text.toString().trim()
            if (recipeName.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a query in the search field.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!isOnline(requireActivity().applicationContext)) {
                Toast.makeText(
                    requireActivity(),
                    "No internet connection found. Please check your connection.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                processingGetRequest()
                edamamViewModel.setQueries(
                    recipeName,
                    false,
                    if (dietLabelUrl.isEmpty()) null else dietLabelUrl,
                    if (healthLabelUrl.isEmpty()) null else healthLabelUrl
                )
            }
        }

        randomRecipeButton.setOnClickListener {
            if (dietLabelUrl.isEmpty() && healthLabelUrl.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please select at least one diet/health label.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!isOnline(requireActivity().applicationContext)) {
                Toast.makeText(
                    requireActivity(),
                    "No internet connection found. Please check your connection.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                processingGetRequest()
                searchRecipeEditTextView.setText("")
                edamamViewModel.setQueries(
                    "",
                    true,
                    if (dietLabelUrl.isEmpty()) null else dietLabelUrl,
                    if (healthLabelUrl.isEmpty()) null else healthLabelUrl
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        edamamViewModel.cancelJobs()
    }

    /**
     * Open a full screen dialog that will show the recipe.
     *
     * @param position the specific row to open the url
     */
    override fun onRecipeClick(position: Int) {
        RecipeWebViewFragment(
            recipesList[position].recipeTitle.toString(),
            recipesList[position].recipeUrl.toString()
        ).display(childFragmentManager)
    }

    override fun onAddEmissionsClick(position: Int) {
        val recipeName = recipesList[position].recipeTitle.toString()
        val score = recipesList[position].recipeScore
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(recipeName)
            .setCancelable(false)
            .setMessage("Are you sure you want to add $score kg of CO₂-eq to your GHG emissions records?")
            .setPositiveButton("yes") { _, _ ->
                if (isOnline(requireActivity().applicationContext)) {
                    dailyScore = dailyScore.add(score)
                    weeklyScore = weeklyScore.add(score)
                    monthlyScore = monthlyScore.add(score)
                    yearlyScore = yearlyScore.add(score)
                    firebaseDatabaseViewModel.updateChildValue("daily", dailyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValue("weekly", weeklyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValue("monthly", monthlyScore.toDouble())
                    firebaseDatabaseViewModel.updateChildValue("yearly", yearlyScore.toDouble())
                    Toast.makeText(
                        requireActivity(),
                        "Your GHG emissions have successfully been updated.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onAddEmissionsClick(position)
                    Toast.makeText(
                        requireActivity(),
                        "No internet connection found. Please check your connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ ->
                return@OnClickListener
            })
            .create()
            .show()
    }

    /**
     * Check if the first image returns a HTTP 403 error and if it does, clear the saved recipes.
     */
    private fun verifyJsonData() {
        try {
            if (recipesList.size > 0) {
                val url = URL(recipesList[0].recipeImage.toString())
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    readTimeout = 5000
                    connectTimeout = 5000
                    requestMethod = "GET"
                }
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                        firebaseDatabaseViewModel.updateChildValue("recipesJsonData", "")
                        recipesList = ArrayList()
                    }
                }
            }
        } catch (error: Exception) {
            toastErrorMessages(
                requireActivity(),
                "Cannot load recipes: No internet connection found. Please check your connection.",
                error.message.toString()
            )
        }
    }

    /**
     * Initialize the RecyclerView.
     */
    private fun initRecyclerView() {
        recipesRecyclerView.apply {
            adapter = RecipeCardAdapter(recipesList, this@RecipesFragment)
            layoutManager = GridLayoutManager(requireActivity(), 2)
            layoutAnimation = animationController
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    /**
     * Hide the keyboard, show a loading spinner, and clear the current list of recipes when a new
     * GET request is being made.
     */
    private fun processingGetRequest() {
        this.requireView().hideKeyboard()
        loadingDialog.startDialog()
        recipesList.clear()
        recipesRecyclerView.adapter?.notifyDataSetChanged()
        firebaseDatabaseViewModel.updateChildValue("recipesJsonData", "")
    }

    /**
     * Let the user know that it could not calculate the CO2 emissions for their input.
     *
     * @param title Title of the dialog to be presented
     * @param message Message to show the user
     */
    private fun noCalculations(title: String, message: String) {
        val noCalcsBuilder = MaterialAlertDialogBuilder(this.requireActivity())
        noCalcsBuilder.apply {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
        return
    }

    companion object {
        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .add(BigDecimalAdapter)
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }

        private val type: Type by lazy {
            Types.newParameterizedType(List::class.java, RecipeCard::class.java)
        }

        private val jsonAdapter: JsonAdapter<ArrayList<RecipeCard>> by lazy {
            moshi.adapter(type)
        }
    }
}
