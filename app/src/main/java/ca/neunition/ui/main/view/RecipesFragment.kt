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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.model.api.Ingredient
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.BigDecimalAdapter
import ca.neunition.ui.main.adapter.RecipeCardAdapter
import ca.neunition.ui.main.viewmodel.EdamamViewModel
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.Constants
import ca.neunition.util.hideKeyboard
import ca.neunition.util.isOnline
import ca.neunition.util.toastErrorMessages
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var selectedLabels = BooleanArray(LABELS.size)

    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")

    private var currentJsonObject = ""
    private lateinit var recipesRecyclerView: RecyclerView
    private var recipesList = ArrayList<RecipeCard>()

    private var mRewardedAd: RewardedAd? = null
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadRewardedAd()
        loadInterstitialAd()

        loadingDialog = LoadingDialog(requireActivity())

        searchRecipeEditTextView = view.findViewById(R.id.search_recipe_edit_text_view)
        searchRecipeButton = view.findViewById(R.id.search_recipe_button)
        randomRecipeButton = view.findViewById(R.id.random_recipe_button)
        labelsTextView = view.findViewById(R.id.select_label_text_view)
        recipesRecyclerView = view.findViewById(R.id.edamam_recipes_recycler_view)

        recipesRecyclerView.apply {
            setHasFixedSize(false)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(requireActivity(), R.anim.recipes_recycler_view_animation)
        }

        firebaseDatabaseViewModel = ViewModelProvider(requireActivity())[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.firebaseUserData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dailyScore = BigDecimal(user.daily.toString())
                weeklyScore = BigDecimal(user.weekly.toString())
                monthlyScore = BigDecimal(user.monthly.toString())
                yearlyScore = BigDecimal(user.yearly.toString())
                if (currentJsonObject != user.recipesJsonData) {
                    if (user.recipesJsonData != "") {
                        recipesList = jsonAdapter.fromJson(user.recipesJsonData)!!
                        verifyJsonData()
                    }
                    initRecyclerView()
                    currentJsonObject = user.recipesJsonData
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
                    noResults("Error", "Oops, something went wrong. Please try again later.")
                }
                it.hits.isEmpty() -> {
                    loadingDialog.dismissDialog()
                    noResults("", "Sorry, we couldn't find any recipes for your search.")
                }
                else -> {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                        for (i in it.hits.indices) {
                            val score = calculateRecipeEmissions(it.hits[i].recipe?.ingredients)

                            val item = RecipeCard(
                                it.hits[i].recipe?.image,
                                it.hits[i].recipe?.label,
                                score,
                                it.hits[i].recipe?.url
                            )

                            recipesList += item
                        }

                        recipesList = recipesList.sortedBy { recipe -> recipe.recipeScore }.toCollection(ArrayList())
                        firebaseDatabaseViewModel.updateChildValue("recipesJsonData", jsonAdapter.toJson(recipesList))

                        withContext(Dispatchers.Main) {
                            loadingDialog.dismissDialog()
                        }
                    }
                }
            }
        }

        labelsTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.apply {
                    setTitle("Select diet/health labels")
                    setCancelable(false)
                    setMultiChoiceItems(LABELS, selectedLabels) { _, i, selectedCheckbox ->
                        if (selectedCheckbox) {
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
                            var choice = LABELS[labelsList[j]]

                            checkedLabels.append(choice)
                            if (j != labelsList.size - 1) {
                                checkedLabels.append(", ")
                            }

                            choice = choice.lowercase()
                            if (choice in DIET_PARAMETERS) {
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
        showInterstitialAd()
        RecipeWebViewFragment(
            recipesList[position].recipeTitle.toString(),
            recipesList[position].recipeUrl.toString()
        ).show(childFragmentManager, RECIPE_WEB_VIEW_TAG)
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
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    /**
     * Hide the keyboard, show a loading spinner, and clear the current list of recipes when a new
     * GET request is being made.
     */
    private fun processingGetRequest() {
        showRewardedVideo()
        this.requireView().hideKeyboard()
        loadingDialog.startDialog()
        recipesList.clear()
        recipesRecyclerView.adapter?.notifyDataSetChanged()
        firebaseDatabaseViewModel.updateChildValue("recipesJsonData", "")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            Glide.get(requireActivity()).clearDiskCache()
        }
        Glide.get(requireActivity()).clearMemory()
    }

    /**
     * Calculate the GHG emissions for the ingredients received.
     *
     * @param ingredients the ingredients to calculate the carbon footprint for
     *
     * @return the calculated CO2 emissions
     */
    private fun calculateRecipeEmissions(ingredients: List<Ingredient>?): BigDecimal {
        var score = BigDecimal("0.00")

        if (ingredients != null) {
            for (ingredient in ingredients) {
                var allWords = "${ingredient.text?.lowercase()} ${ingredient.foodCategory?.lowercase()}"
                allWords = Regex("[^-./%\\w\\d\\p{L}\\p{M} ]").replace(allWords, "")
                allWords = Regex("[-]").replace(allWords, " ")
                val keyWords = allWords.split("\\s".toRegex())

                for (word in keyWords.indices) {
                    if (keyWords[word] in Constants.TWO_WORD_INGREDIENTS && word + 1 < keyWords.size && "${keyWords[word]} ${keyWords[word + 1]}" in Constants.INGREDIENTS) {
                        score = score.add(
                            BigDecimal(Constants.INGREDIENTS["${keyWords[word]} ${keyWords[word + 1]}"].toString()).multiply(
                                BigDecimal(ingredient.weight.toString())
                            )
                        )
                        break
                    } else if (keyWords[word] in Constants.THREE_WORD_INGREDIENTS && word + 2 < keyWords.size && "${keyWords[word]} ${keyWords[word + 1]} ${keyWords[word + 2]}" in Constants.INGREDIENTS) {
                        score = score.add(
                            BigDecimal(Constants.INGREDIENTS["${keyWords[word]} ${keyWords[word + 1]} ${keyWords[word + 2]}"].toString()).multiply(
                                BigDecimal(ingredient.weight.toString())
                            )
                        )
                        break
                    } else if (keyWords[word] in Constants.INGREDIENTS) {
                        score = score.add(
                            BigDecimal(Constants.INGREDIENTS[keyWords[word]].toString()).multiply(
                                BigDecimal(ingredient.weight.toString())
                            )
                        )
                        break
                    }
                }
            }
        }

        return score.setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * Let the user know that it could not find any recipes for their search.
     *
     * @param title Title of the dialog to be presented
     * @param message Message to show the user
     */
    private fun noResults(title: String, message: String) {
        val noResultsBuilder = MaterialAlertDialogBuilder(this.requireActivity())
        noResultsBuilder.apply {
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

    private fun loadRewardedAd() {
        if (mRewardedAd == null) {
            RewardedAd.load(
                requireActivity(),
                Constants.REWARDED_AD_UNIT_ID,
                Constants.AD_REQUEST,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        super.onAdLoaded(rewardedAd)
                        mRewardedAd = rewardedAd
                    }
                }
            )
        }
    }

    private fun showRewardedVideo() {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        mRewardedAd = null
                        loadRewardedAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        mRewardedAd = null
                    }
                }

            mRewardedAd?.show(requireActivity()) {}
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireActivity(),
            Constants.RECIPES_INTERSTITIAL_AD_UNIT_ID,
            Constants.AD_REQUEST,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadInterstitialAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        mInterstitialAd = null
                    }
                }
            mInterstitialAd?.show(requireActivity())
        } else {
            loadInterstitialAd()
        }
    }

    companion object {
        private val LABELS: Array<String> by lazy {
            arrayOf(
                "Alcohol-Cocktail",
                "Alcohol-Free",
                "Balanced",
                "Celery-Free",
                "Crustacean-Free",
                "Dairy-Free",
                "Egg-Free",
                "Fish-Free",
                "Gluten-Free",
                "High-Fiber",
                "High-Protein",
                "Immuno-Supportive",
                "Keto-Friendly",
                "Kidney-Friendly",
                "Kosher",
                "Low-Carb",
                "Low-Fat",
                "Low-Potassium",
                "Low-Sodium",
                "Low-Sugar",
                "Lupine-Free",
                "Mollusk-Free",
                "Mustard-Free",
                "Paleo",
                "Peanut-Free",
                "Pescatarian",
                "Pork-Free",
                "Red-Meat-Free",
                "Sesame-Free",
                "Shellfish-Free",
                "Soy-Free",
                "Sugar-Conscious",
                "Sulfite-Free",
                "Tree-Nut-Free",
                "Vegan",
                "Vegetarian",
                "Wheat-Free"
            )
        }

        private val DIET_PARAMETERS: HashSet<String> by lazy {
            hashSetOf(
                "balanced",
                "high-fiber",
                "high-protein",
                "low-carb",
                "low-fat",
                "low-sodium"
            )
        }

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

        private val RECIPE_WEB_VIEW_TAG: String by lazy { "RECIPE_WEB_VIEW" }
    }
}
