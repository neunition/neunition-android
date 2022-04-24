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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.IngredientCard
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.IngredientAdapter
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue

class IngredientsEmissionsFragment : Fragment(), IngredientAdapter.OnItemClickListener {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    private lateinit var loadingDialog: LoadingDialog

    // Add ingredient to list
    private lateinit var ingredientTextView: AppCompatEditText
    private lateinit var weightTextView: AppCompatEditText
    private lateinit var weightDropDown: TextInputLayout
    private lateinit var autoCompleteWeights: MaterialAutoCompleteTextView
    private lateinit var addButton: AppCompatButton

    // Ingredient with its CO₂-eq score
    private lateinit var currentEmissionsTextView: AppCompatTextView
    private var currentEmissionsScore = BigDecimal("0.00")
    private var ingredientEmissionsList = ArrayList<IngredientCard>()
    private val adapter = IngredientAdapter(ingredientEmissionsList, this)
    private lateinit var recyclerView: RecyclerView

    private var manualSubmission = true

    private lateinit var uploadIngredientsPhoto: AppCompatImageButton

    // GHG scores
    private var dailyScore = BigDecimal("0.00")
    private var weeklyScore = BigDecimal("0.00")
    private var monthlyScore = BigDecimal("0.00")
    private var yearlyScore = BigDecimal("0.00")
    private lateinit var addEmissionsButton: AppCompatImageButton

    private lateinit var clearAllButton: AppCompatImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredients_emissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        // Get the user's info from the Firebase Realtime Database
        firebaseDatabaseViewModel = ViewModelProvider(this).get(FirebaseDatabaseViewModel::class.java)
        firebaseDatabaseViewModel.getUsersLiveData().observe(viewLifecycleOwner) { users ->
            if (users != null) {
                dailyScore = BigDecimal(users.daily.toString())
                weeklyScore = BigDecimal(users.weekly.toString())
                monthlyScore = BigDecimal(users.monthly.toString())
                yearlyScore = BigDecimal(users.yearly.toString())
            }
        }

        ingredientTextView = view.findViewById(R.id.ingredient_text_view)
        weightTextView = view.findViewById(R.id.weight_text_view)
        weightDropDown = view.findViewById(R.id.drop_down_menu)
        autoCompleteWeights = view.findViewById(R.id.auto_complete_weights)
        addButton = view.findViewById(R.id.plus_button)
        currentEmissionsTextView = view.findViewById(R.id.current_score_text_view)
        recyclerView = view.findViewById(R.id.ingredients_recycler_view)
        uploadIngredientsPhoto = view.findViewById(R.id.upload_ingredients_photo)
        addEmissionsButton = view.findViewById(R.id.add_emissions_button)
        clearAllButton = view.findViewById(R.id.clear_all_button)

        // Drop down menu options
        val weightsAdapter = ArrayAdapter(requireActivity(), R.layout.list_weights, Constants.WEIGHT_OPTIONS)
        (weightDropDown.editText as? AutoCompleteTextView)?.setAdapter(weightsAdapter)

        // Add the ingredient to the RecyclerView
        addButton.setOnClickListener {
            this.requireView().hideKeyboard()
            verifyAndCalculateIngredientCO2()
        }

        currentEmissionsTextView.apply {
            setSpannableFactory(spannableFactory)
            setText(
                scoreColourChange(
                    requireActivity(),
                    "Current GHG Emissions:",
                    currentEmissionsScore,
                    "1.08",
                    "1.61"
                ),
                TextView.BufferType.SPANNABLE
            )
        }

        val ingredientsImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                if (res.resultCode == Activity.RESULT_OK && res.data != null) {
                    loadingDialog.startDialog()
                    lifecycleScope.launch(Dispatchers.IO) {
                        val recognizer =
                            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        // Proceed and check what the selected image was
                        val ingrImageUri = res.data!!.data!!
                        val image = InputImage.fromFilePath(
                            requireActivity().applicationContext,
                            ingrImageUri
                        )
                        recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                manualSubmission = false
                                for (block in visionText.textBlocks) {
                                    for (line in block.lines) {
                                        var ingredient = ""
                                        var ingredientWeight = 0.0
                                        var ingredientMeasurement = ""
                                        for (element in line.elements) {
                                            val elementText =
                                                element.text.replace("[^A-Za-z0-9 ]".toRegex(), "")
                                                    .lowercase()
                                            if (element.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                                ingredientWeight += element.text.toDouble().absoluteValue
                                            } else if (element.text.matches("\\d{1,5}([.]\\d{1,3}|(\\s\\d{1,5})?[/]\\d{1,3})?".toRegex())) {
                                                ingredientWeight += convertFractionToDecimal(element.text).absoluteValue
                                            } else if (elementText in Constants.WEIGHTS && ingredientMeasurement == "") {
                                                ingredientMeasurement = elementText.trim()
                                            } else if (elementText in Constants.INGREDIENTS) {
                                                ingredient = elementText.trim()
                                            }
                                        }
                                        if ((ingredientMeasurement == "egg" || ingredientMeasurement == "eggs") && ingredientWeight != 0.0) {
                                            entireIngredientCO2Calculation(
                                                ingredientMeasurement,
                                                ingredientWeight.toString(),
                                                ingredientMeasurement
                                            )
                                        } else if (ingredient != "" && ingredientWeight != 0.0 && ingredientMeasurement != "") {
                                            entireIngredientCO2Calculation(
                                                line.text.trim(),
                                                ingredientWeight.toString(),
                                                ingredientMeasurement
                                            )
                                        } else {
                                            entireIngredientCO2Calculation(
                                                line.text.trim(),
                                                "0.0",
                                                ""
                                            )
                                        }
                                    }
                                }
                                loadingDialog.dismissDialog()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireActivity(),
                                    "Failed to process the image: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                loadingDialog.dismissDialog()
                            }
                    }
                }
            }
        // Automatically add ingredients to the RecyclerView
        uploadIngredientsPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            ingredientsImageLauncher.launch(intent)
        }

        // Add emissions to user's daily, weekly, and monthly scores
        addEmissionsButton.setOnClickListener {
            updateUserEmissions()
        }

        clearAllButton.setOnClickListener {
            clearRecyclerView()
        }

        // Hide the keyboard when the user picks the weight type
        autoCompleteWeights.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.hideKeyboard() // Using view extension function
            }
        }
    }

    /**
     * Calculate the CO2 emissions for the ingredient the user entered.
     */
    private fun verifyAndCalculateIngredientCO2() {
        val ingr = ingredientTextView.text.toString().lowercase().trim()
        val ingrWeight = weightTextView.text.toString()
        val selectedWeight = weightDropDown.editText?.text.toString()

        if (ingr.isEmpty() || ingrWeight.isEmpty() || selectedWeight.isEmpty()) {
            Toast.makeText(
                requireActivity(),
                "Please fill in all required fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            manualSubmission = true
        }

        entireIngredientCO2Calculation(ingr, ingrWeight, selectedWeight)

        ingredientTextView.setText("")
        weightTextView.setText("")
        autoCompleteWeights.text = null
    }

    private fun convertFractionToDecimal(ratio: String): Double {
        return if (ratio.contains("/")) {
            val rat: Array<String> = ratio.split("/").toTypedArray()
            rat[0].toDouble() / rat[1].toDouble()
        } else {
            ratio.toDouble()
        }
    }

    private fun entireIngredientCO2Calculation(
        ingr: String,
        ingrWeight: String,
        selectedWeight: String
    ) {
        val re = Regex("[^a-zA-Z ]")
        val mainFood = re.replace(ingr, "")
        val mainFoods = mainFood.split("\\s+".toRegex())
        val allFoodWords: MutableList<String> = mainFoods.toMutableList()

        // Make sure the weight of the ingredient is in grams
        val weightOfIngr = gramsConverter(BigDecimal(ingrWeight), selectedWeight)

        var ingrExists = false
        // Calculate carbon food footprint for the ingredient

        if (selectedWeight != "") {
            for (foodWord in allFoodWords) {
                if (foodWord in Constants.INGREDIENTS) {
                    var correctCalc = BigDecimal(Constants.INGREDIENTS[foodWord]!!).multiply(weightOfIngr)
                    correctCalc = correctCalc.setScale(5, RoundingMode.HALF_UP)
                    // Add the ingredient to the RecyclerView
                    if (ingr == "egg" || ingr == "eggs") {
                        addIngredientGHGCard(
                            "$ingrWeight $ingr = $correctCalc kg of CO₂-eq",
                            correctCalc,
                            false
                        )
                    } else {
                        if (manualSubmission) {
                            addIngredientGHGCard(
                                "$ingrWeight $selectedWeight of $ingr = $correctCalc kg of CO₂-eq",
                                correctCalc,
                                false
                            )
                        } else {
                            addIngredientGHGCard(
                                "$ingr = $correctCalc kg of CO₂-eq",
                                correctCalc,
                                false
                            )
                        }
                    }
                    currentEmissionsScore =
                        currentEmissionsScore.add(correctCalc.setScale(2, RoundingMode.HALF_UP))
                    currentEmissionsTextView.setText(
                        scoreColourChange(
                            requireActivity(),
                            "Current GHG Emissions:",
                            currentEmissionsScore,
                            "1.08",
                            "1.61"
                        ), TextView.BufferType.SPANNABLE
                    )
                    ingrExists = true
                    break
                }
            }
        }

        // CO2 emissions data for the ingredient doesn't exist
        // val properIngr = allFoodWords.joinToString(separator = " ")
        if (!ingrExists && !manualSubmission) {
            addIngredientGHGCard(
                "$ingr = 0.00 kg of CO₂-eq",
                BigDecimal("0.0"),
                true
            )
        } else if (!ingrExists && manualSubmission) {
            addIngredientGHGCard(
                "$ingrWeight $selectedWeight of $ingr = 0.00 kg of CO₂-eq",
                BigDecimal("0.0"),
                false
            )
        }
    }

    /**
     * Convert the user's ingredient weight to grams.
     *
     * @param weight the weight of the ingredient
     * @param measurement the weight type of the ingredient
     *
     * @return the new weight that is in grams
     */
    private fun gramsConverter(weight: BigDecimal, measurement: String): BigDecimal {
        var newWeight = BigDecimal("0.00")
        when (measurement) {
            "mg", "milligram", "milligrams" -> newWeight = weight.divide(BigDecimal("1000"))
            "g", "gram", "grams" -> newWeight = weight
            "kg", "kgs", "kilogram", "kilograms", "kilo", "kilos" -> newWeight = weight.multiply(BigDecimal("1000"))
            "tsp", "tsps", "teaspoon", "teaspoons" -> newWeight = weight.multiply(BigDecimal("4.928921594"))
            "tbsp", "tbsps", "tablespoon", "tablespoons" -> newWeight = weight.multiply(BigDecimal("14.78676"))
            "cup", "cups" -> newWeight = weight.multiply(BigDecimal("236.58824"))
            "lb", "lbs", "pound", "pounds" -> newWeight = weight.multiply(BigDecimal("453.59237"))
            "oz", "ounce", "ounces" -> newWeight = weight.multiply(BigDecimal("28.34952"))
            "fl oz" -> newWeight = weight.multiply(BigDecimal("29.5735295625"))
            "ml", "milliliter", "milliliters", "millilitre", "millilitres" -> newWeight = weight
            "l", "liter", "liters", "litre", "litres" -> newWeight = weight.multiply(BigDecimal("1000"))
            "gal", "gallon", "gallons" -> newWeight = weight.multiply(BigDecimal("3785.411784"))
            "egg", "eggs" -> newWeight = weight
        }
        return newWeight
    }

    private fun addIngredientGHGCard(noIngrMsg: String, scoreToDisplay: BigDecimal, italicizeText: Boolean) {
        val item = IngredientCard(noIngrMsg, scoreToDisplay, italicizeText)
        ingredientEmissionsList += item
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)
    }

    /**
     * Add the user's ingredients' emissions to their daily, weekly, and monthly scores.
     */
    private fun updateUserEmissions() {
        if (currentEmissionsScore.compareTo(BigDecimal("0.00")) != 0 && isOnline(requireActivity().applicationContext)) {
            dailyScore = dailyScore.add(currentEmissionsScore)
            weeklyScore = weeklyScore.add(currentEmissionsScore)
            monthlyScore = monthlyScore.add(currentEmissionsScore)
            yearlyScore = yearlyScore.add(currentEmissionsScore)
            firebaseDatabaseViewModel.updateChildValues("daily", dailyScore.toDouble())
            firebaseDatabaseViewModel.updateChildValues("weekly", weeklyScore.toDouble())
            firebaseDatabaseViewModel.updateChildValues("monthly", monthlyScore.toDouble())
            firebaseDatabaseViewModel.updateChildValues("yearly", yearlyScore.toDouble())
            clearRecyclerView()
            Toast.makeText(
                requireActivity(),
                "Thank you for your submission! Your GHG emissions have successfully been updated.",
                Toast.LENGTH_LONG
            ).show()
        } else if (ingredientEmissionsList.isNotEmpty() && isOnline(requireActivity().applicationContext)) {
            clearRecyclerView()
            Toast.makeText(
                requireActivity(),
                "Thank you for your submission! Your GHG emissions have successfully been updated.",
                Toast.LENGTH_LONG
            ).show()
        } else if (!isOnline(requireActivity().applicationContext)) {
            Toast.makeText(
                requireActivity(),
                "No internet connection found. Please check your connection.",
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
        ingredientEmissionsList.clear()
        recyclerView.adapter?.notifyDataSetChanged()
        currentEmissionsScore = BigDecimal("0.00")
        currentEmissionsTextView.setText(
            scoreColourChange(
                requireActivity(),
                "Current GHG Emissions:",
                currentEmissionsScore,
                "1.08",
                "1.61"
            ), TextView.BufferType.SPANNABLE
        )
    }

    /**
     * Remove an ingredient from the RecyclerView.
     *
     * @param position the specific ingredient to be removed
     */
    override fun onDeleteClick(position: Int) {
        currentEmissionsScore =
            currentEmissionsScore.subtract(ingredientEmissionsList[position].weight)
        currentEmissionsScore = currentEmissionsScore.setScale(2, RoundingMode.HALF_UP)
        currentEmissionsTextView.setText(
            scoreColourChange(
                requireActivity(),
                "Current GHG Emissions:",
                currentEmissionsScore,
                "1.08",
                "1.61"
            ), TextView.BufferType.SPANNABLE
        )
        ingredientEmissionsList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }
}