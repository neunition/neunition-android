/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Allow the user to calculate the GHG emissions for each ingredient they enter through manually
 * typing it or by uploading an image.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.IngredientCard
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.IngredientAdapter
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue

class IngredientsEmissionsFragment : Fragment(), IngredientAdapter.OnClickListener {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    private lateinit var loadingDialog: LoadingDialog

    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")

    private lateinit var ingredientTextView: AppCompatEditText
    private lateinit var weightTextView: AppCompatEditText
    private lateinit var weightDropDown: TextInputLayout
    private lateinit var autoCompleteWeights: MaterialAutoCompleteTextView
    private lateinit var addIngredientButton: AppCompatButton

    private lateinit var currentEmissionsTextView: AppCompatTextView
    private var currentEmissionsScore = BigDecimal("0.00")
    private var ingredientsEmissionsList = ArrayList<IngredientCard>()
    private lateinit var ingredientsRecyclerView: RecyclerView

    private var manualSubmission = true

    private lateinit var uploadIngredientsPhoto: AppCompatImageButton
    private lateinit var addEmissionsButton: AppCompatImageButton
    private lateinit var clearAllButton: AppCompatImageButton

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients_emissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadInterstitialAd()

        loadingDialog = LoadingDialog(requireActivity())

        firebaseDatabaseViewModel = ViewModelProvider(this)[FirebaseDatabaseViewModel::class.java]
        firebaseDatabaseViewModel.firebaseUserData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dailyScore = BigDecimal(user.daily.toString())
                weeklyScore = BigDecimal(user.weekly.toString())
                monthlyScore = BigDecimal(user.monthly.toString())
                yearlyScore = BigDecimal(user.yearly.toString())
            }
        }

        ingredientTextView = view.findViewById(R.id.ingredient_text_view)
        weightTextView = view.findViewById(R.id.weight_text_view)
        weightDropDown = view.findViewById(R.id.drop_down_menu)
        autoCompleteWeights = view.findViewById(R.id.auto_complete_weights)
        addIngredientButton = view.findViewById(R.id.plus_button)

        currentEmissionsTextView = view.findViewById(R.id.current_score_text_view)
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view)

        uploadIngredientsPhoto = view.findViewById(R.id.upload_ingredients_photo)
        addEmissionsButton = view.findViewById(R.id.add_emissions_button)
        clearAllButton = view.findViewById(R.id.clear_all_button)

        val weightsAdapter = ArrayAdapter(requireActivity(), R.layout.list_weights, WEIGHT_OPTIONS)
        (weightDropDown.editText as? AutoCompleteTextView)?.setAdapter(weightsAdapter)
        autoCompleteWeights.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.hideKeyboard()
            }
        }

        addIngredientButton.setOnClickListener {
            this.requireView().hideKeyboard()
            verifyIngredientSubmission()
        }

        ingredientsRecyclerView.apply {
            adapter = IngredientAdapter(ingredientsEmissionsList, this@IngredientsEmissionsFragment)
            setHasFixedSize(true)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireActivity(),
                R.anim.ingredients_recycler_view_animation
            )
        }

        currentEmissionsTextView.apply {
            setSpannableFactory(spannableFactory)
            setText(
                currentScoreColourChange(
                    requireActivity(),
                    "Current GHG Emissions",
                    currentEmissionsScore,
                    "1.85",
                    "2.05"
                ),
                TextView.BufferType.SPANNABLE
            )
        }

        val ingredientsImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                if (res.resultCode == Activity.RESULT_OK && res.data != null) {
                    loadingDialog.startDialog()
                    val image = InputImage.fromFilePath(requireActivity(), res.data!!.data!!)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val visionText = recognizer.process(image).await()
                            manualSubmission = false
                            withContext(Dispatchers.Default) {
                                processTextFromImage(visionText)
                            }
                            withContext(Dispatchers.Main) {
                                loadingDialog.dismissDialog()
                            }
                        } catch (error: Exception) {
                            withContext(Dispatchers.Main) {
                                loadingDialog.dismissDialog()
                                Toast.makeText(
                                    requireActivity(),
                                    "Failed to process the image: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

        uploadIngredientsPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            ingredientsImageLauncher.launch(intent)
        }

        addEmissionsButton.setOnClickListener {
            updateUserEmissions()
        }

        clearAllButton.setOnClickListener {
            clearRecyclerView()
        }
    }

    /**
     * Remove an ingredient from the RecyclerView.
     *
     * @param position the specific ingredient to be removed
     */
    override fun onDeleteClick(position: Int) {
        currentEmissionsScore =
            currentEmissionsScore.subtract(ingredientsEmissionsList[position].weight)
        currentEmissionsScore = currentEmissionsScore.setScale(2, RoundingMode.HALF_UP)
        currentEmissionsTextView.setText(
            currentScoreColourChange(
                requireActivity(),
                "Current GHG Emissions",
                currentEmissionsScore,
                "1.85",
                "2.05"
            ),
            TextView.BufferType.SPANNABLE
        )
        ingredientsEmissionsList.removeAt(position)
        ingredientsRecyclerView.adapter?.notifyItemRemoved(position)
    }

    /**
     * Verify the user has filled all required fields and if they have, calculate the emissions.
     */
    private fun verifyIngredientSubmission() {
        val ingredient = ingredientTextView.text.toString().lowercase().trim()
        val ingredientWeight = weightTextView.text.toString()
        val selectedWeight = weightDropDown.editText?.text.toString().lowercase()

        if (ingredient.isEmpty() || ingredientWeight.isEmpty() || selectedWeight.isEmpty()) {
            Toast.makeText(
                requireActivity(),
                "Please fill in all required fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            manualSubmission = true
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            calculateEmissionsForIngredient(ingredient, ingredientWeight, selectedWeight)
        }

        ingredientTextView.setText("")
        weightTextView.setText("")
        autoCompleteWeights.text = null
    }

    /**
     * Extract the text from the image and see if it has the required info to calculate the GHG
     * emissions.
     *
     * @param visionText The Text object that contains the full text recognized in the image
     */
    private fun processTextFromImage(visionText: Text) {
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                var ingredientWeight = 0.0
                var ingredientMeasurement = ""

                for (element in line.elements) {
                    val elementText = element.text
                        .replace("[^A-Za-z0-9 ]".toRegex(), "")
                        .lowercase()

                    if (element.text.matches("-?\\d*(\\.\\d+)?".toRegex())) {
                        ingredientWeight += element.text.toDouble().absoluteValue
                    } else if (element.text.matches("\\d{1,5}([.]\\d{1,3}|(\\s\\d{1,5})?[/]\\d{1,3})?".toRegex())) {
                        ingredientWeight += convertFractionToDecimal(element.text).absoluteValue
                    } else if (elementText in WEIGHTS_IN_IMAGES) {
                        if (ingredientMeasurement != "") {
                            ingredientMeasurement = ""
                            break
                        } else {
                            ingredientMeasurement = elementText.trim()
                        }
                    }
                }

                if ((ingredientMeasurement == "egg" || ingredientMeasurement == "eggs") && ingredientWeight != 0.0) {
                    calculateEmissionsForIngredient(
                        ingredientMeasurement,
                        ingredientWeight.toString(),
                        ingredientMeasurement
                    )
                } else if (ingredientWeight != 0.0 && ingredientMeasurement != "") {
                    calculateEmissionsForIngredient(
                        line.text.trim(),
                        ingredientWeight.toString(),
                        ingredientMeasurement
                    )
                } else {
                    calculateEmissionsForIngredient(
                        line.text.trim(),
                        "0.00",
                        ""
                    )
                }
            }
        }
    }

    /**
     * Convert a fraction into a decimal.
     *
     * @param ratio The fraction
     *
     * @return The decimal converted from the given fraction
     */
    private fun convertFractionToDecimal(ratio: String): Double {
        return if (ratio.contains("/")) {
            val rat: Array<String> = ratio.split("/").toTypedArray()
            rat[0].toDouble() / rat[1].toDouble()
        } else {
            ratio.toDouble()
        }
    }

    /**
     * Calculate the GHG emissions for an individual ingredient.
     *
     * @param ingredient The ingredient to be calculated
     * @param ingredientWeight The weight of the ingredient
     * @param selectedWeight The unit for the weight
     */
    private fun calculateEmissionsForIngredient(
        ingredient: String,
        ingredientWeight: String,
        selectedWeight: String
    ) {
        var score = BigDecimal("0.00")
        var ingredientExists = false

        if (selectedWeight != "") {
            var allWords = Regex("[^-./%\\w\\d\\p{L}\\p{M} ]").replace(ingredient.lowercase(), "")
            allWords = Regex("[-]").replace(allWords, " ")
            val keyWords = allWords.split("\\s".toRegex())

            val ingredientInGrams = gramsConverter(BigDecimal(ingredientWeight), selectedWeight)

            for (word in keyWords.indices) {
                if (keyWords[word] in Constants.TWO_WORD_INGREDIENTS && word + 1 < keyWords.size && "${keyWords[word]} ${keyWords[word + 1]}" in Constants.INGREDIENTS) {
                    score = score.add(
                        BigDecimal(
                            Constants.INGREDIENTS["${keyWords[word]} ${keyWords[word + 1]}"].toString()
                        ).multiply(ingredientInGrams)
                    )
                    ingredientExists = true
                    break
                } else if (keyWords[word] in Constants.THREE_WORD_INGREDIENTS && word + 2 < keyWords.size && "${keyWords[word]} ${keyWords[word + 1]} ${keyWords[word + 2]}" in Constants.INGREDIENTS) {
                    score = score.add(
                        BigDecimal(
                            Constants.INGREDIENTS["${keyWords[word]} ${keyWords[word + 1]} ${keyWords[word + 2]}"].toString()
                        ).multiply(ingredientInGrams)
                    )
                    ingredientExists = true
                    break
                } else if (keyWords[word] in Constants.INGREDIENTS) {
                    score = score.add(
                        BigDecimal(
                            Constants.INGREDIENTS[keyWords[word]].toString()
                        ).multiply(ingredientInGrams)
                    )
                    ingredientExists = true
                    break
                }
            }
        }

        if (ingredientExists) {
            score = score.setScale(5, RoundingMode.HALF_UP)
            if (selectedWeight == "egg" || selectedWeight == "eggs") {
                addIngredientGHGCard(
                    "$ingredientWeight $selectedWeight = $score kg of CO₂-eq",
                    score,
                    false
                )
            } else {
                if (manualSubmission) {
                    addIngredientGHGCard(
                        "$ingredientWeight $selectedWeight of $ingredient = $score kg of CO₂-eq",
                        score,
                        false
                    )
                } else {
                    addIngredientGHGCard(
                        "$ingredient = $score kg of CO₂-eq",
                        score,
                        false
                    )
                }
            }
        } else {
            if (manualSubmission) {
                addIngredientGHGCard(
                    "$ingredientWeight $selectedWeight of $ingredient = 0.00 kg of CO₂-eq",
                    BigDecimal("0.00"),
                    false
                )
            } else {
                addIngredientGHGCard(
                    "$ingredient = 0.00 kg of CO₂-eq",
                    BigDecimal("0.00"),
                    true
                )
            }
        }
    }

    /**
     * Convert the user's ingredient weight to grams.
     *
     * @param weight Weight of the ingredient
     * @param measurement Weight type of the ingredient
     *
     * @return New weight that is in grams.
     */
    private fun gramsConverter(weight: BigDecimal, measurement: String): BigDecimal {
        var newWeight = BigDecimal("0.00")
        when (measurement) {
            "mg", "milligram", "milligrams" -> newWeight = weight.divide(BigDecimal("1000"))
            "g", "gm", "gms", "gram", "grams" -> newWeight = weight
            "kg", "kgs", "kilogram", "kilograms", "kilo", "kilos" -> newWeight = weight.multiply(BigDecimal("1000"))
            "tsp", "tsps", "teaspoon", "teaspoons" -> newWeight = weight.multiply(BigDecimal("4.928921594"))
            "tbsp", "tbsps", "tablespoon", "tablespoons" -> newWeight = weight.multiply(BigDecimal("14.78676"))
            "cup", "cups" -> newWeight = weight.multiply(BigDecimal("236.58824"))
            "lb", "lbs", "pound", "pounds" -> newWeight = weight.multiply(BigDecimal("453.59237"))
            "oz", "ounce", "ounces" -> newWeight = weight.multiply(BigDecimal("28.34952"))
            "ml", "milliliter", "milliliters", "millilitre", "millilitres" -> newWeight = weight
            "l", "liter", "liters", "litre", "litres" -> newWeight = weight.multiply(BigDecimal("1000"))
            "gal", "gallon", "gallons" -> newWeight = weight.multiply(BigDecimal("3785.411784"))
            "egg", "eggs" -> newWeight = weight.multiply(BigDecimal("50"))
            "clove", "cloves" -> newWeight = weight.multiply(BigDecimal("4"))
        }
        return newWeight
    }

    /**
     * Add the card with the GHG emissions for a ingredient to the list.
     *
     * @param ingredient The ingredient that was calculated
     * @param score The GHG emissions associated with the ingredient
     * @param italicizeText Italicize the text or not
     */
    private fun addIngredientGHGCard(
        ingredient: String,
        score: BigDecimal,
        italicizeText: Boolean
    ) = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
        currentEmissionsScore = currentEmissionsScore.add(score.setScale(2, RoundingMode.HALF_UP))
        currentEmissionsTextView.setText(
            currentScoreColourChange(
                requireActivity(),
                "Current GHG Emissions",
                currentEmissionsScore,
                "1.85",
                "2.05"
            ),
            TextView.BufferType.SPANNABLE
        )

        val item = IngredientCard(ingredient, score, italicizeText)
        ingredientsEmissionsList.add(item)
        ingredientsRecyclerView.apply {
            adapter?.notifyItemInserted(ingredientsEmissionsList.size - 1)
            if (!manualSubmission) scheduleLayoutAnimation()
        }
    }

    /**
     * Add the user's ingredients' emissions to their daily, weekly, monthly, and yearly scores.
     */
    private fun updateUserEmissions() {
        if (!isOnline(requireActivity().applicationContext)) {
            Toast.makeText(
                requireActivity(),
                "No internet connection found. Please check your connection.",
                Toast.LENGTH_LONG
            ).show()
        } else if (currentEmissionsScore.compareTo(BigDecimal("0.00")) != 0) {
            showInterstitialAd()
        } else if (ingredientsEmissionsList.isNotEmpty()) {
            clearRecyclerView()
            Toast.makeText(
                requireActivity(),
                "Thank you for your submission! Your GHG emissions have successfully been updated.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Remove all ingredients in the RecyclerView.
     */
    private fun clearRecyclerView() {
        ingredientTextView.setText("")
        weightTextView.setText("")
        autoCompleteWeights.text = null
        ingredientsEmissionsList.clear()
        ingredientsRecyclerView.adapter?.notifyDataSetChanged()
        currentEmissionsScore = BigDecimal("0.00")
        currentEmissionsTextView.setText(
            currentScoreColourChange(
                requireActivity(),
                "Current GHG Emissions",
                currentEmissionsScore,
                "1.85",
                "2.05"
            ),
            TextView.BufferType.SPANNABLE
        )
    }

    /**
     * Load a interstitial ad into the app.
     */
    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireActivity(),
            Constants.INGREDIENTS_INTERSTITIAL_AD_UNIT_ID,
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

    /**
     * Show a interstitial ad to the user.
     */
    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadInterstitialAd()
                        Toast.makeText(
                            requireActivity(),
                            "Thank you for your submission! Your GHG emissions have successfully been updated.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        mInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        dailyScore = dailyScore.add(currentEmissionsScore)
                        weeklyScore = weeklyScore.add(currentEmissionsScore)
                        monthlyScore = monthlyScore.add(currentEmissionsScore)
                        yearlyScore = yearlyScore.add(currentEmissionsScore)
                        firebaseDatabaseViewModel.updateChildValue("daily", dailyScore.toDouble())
                        firebaseDatabaseViewModel.updateChildValue("weekly", weeklyScore.toDouble())
                        firebaseDatabaseViewModel.updateChildValue("monthly", monthlyScore.toDouble())
                        firebaseDatabaseViewModel.updateChildValue("yearly", yearlyScore.toDouble())
                        clearRecyclerView()
                    }
                }
            mInterstitialAd?.show(requireActivity())
        } else {
            loadInterstitialAd()
        }
    }

    companion object {
        private val WEIGHT_OPTIONS: List<String> by lazy {
            listOf(
                "mg",
                "g",
                "kg",
                "tsp",
                "tbsp",
                "cup",
                "lb",
                "oz",
                "mL",
                "L",
                "gal",
                "egg(s)",
                "clove(s)"
            )
        }

        private val WEIGHTS_IN_IMAGES: HashSet<String> by lazy {
            hashSetOf(
                "mg",
                "milligram",
                "milligrams",
                "g",
                "gm",
                "gms",
                "gram",
                "grams",
                "kg",
                "kgs",
                "kilogram",
                "kilograms",
                "kilo",
                "kilos",
                "tsp",
                "tsps",
                "teaspoon",
                "teaspoons",
                "tbsp",
                "tbsps",
                "tablespoon",
                "tablespoons",
                "cup",
                "cups",
                "lb",
                "lbs",
                "pound",
                "pounds",
                "oz",
                "ounce",
                "ounces",
                "ml",
                "milliliter",
                "milliliters",
                "millilitre",
                "millilitres",
                "l",
                "liter",
                "liters",
                "litre",
                "litres",
                "gal",
                "gallon",
                "gallons",
                "egg",
                "eggs",
                "clove",
                "cloves"
            )
        }

        private val recognizer: TextRecognizer by lazy {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }
    }
}
