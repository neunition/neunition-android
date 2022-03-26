/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Allow the user to search for recipes that also shows their respective GHG emissions.
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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.EdamamViewModel
import ca.neunition.R
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.ui.common.dialog.LoadingDialog
import ca.neunition.ui.main.adapter.BigDecimalAdapter
import ca.neunition.ui.main.adapter.RecipeAdapter
import ca.neunition.ui.main.viewmodel.FirebaseDatabaseViewModel
import ca.neunition.util.Constants
import ca.neunition.util.hideKeyboard
import ca.neunition.util.noCalculations
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class RecipesFragment : Fragment(), RecipeAdapter.OnRecipeClickListener {
    private lateinit var firebaseDatabaseViewModel: FirebaseDatabaseViewModel

    private val foods by lazy { Constants.MEALS }

    // Edamam API
    private lateinit var edamamViewModel: EdamamViewModel

    // Loading screen
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var searchRecipeEditTextView: AppCompatEditText
    private lateinit var searchRecipeButton: AppCompatImageButton
    private lateinit var randomRecipeButton: AppCompatImageButton

    private var dietLabelUrl = arrayOf<String>()
    private var healthLabelUrl = arrayOf<String>()

    private lateinit var label: AppCompatTextView
    private var labelsList = arrayListOf<Int>()
    internal val labelsArray by lazy { Constants.LABELS }
    private lateinit var checkedLabels: StringBuilder
    private var selectedLabels = BooleanArray(labelsArray.size)

    private var recipesChanged = ""
    private lateinit var recipeRecyclerView: RecyclerView
    private var recipesList = ArrayList<RecipeCard>()
    private lateinit var adapter: RecipeAdapter
    private lateinit var recyclerViewLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        recipeRecyclerView = view.findViewById(R.id.edamam_recipes_recycler_view)
        recyclerViewLayoutManager = GridLayoutManager(requireActivity(), 2)

        firebaseDatabaseViewModel =
            ViewModelProvider(this).get(FirebaseDatabaseViewModel::class.java)
        firebaseDatabaseViewModel.getUsersLiveData().observe(viewLifecycleOwner) { users ->
            if (users != null) {
                requireActivity().runOnUiThread(kotlinx.coroutines.Runnable {
                    run {
                        if (recipesChanged != users.recipeJsonData) {
                            loadData(users.recipeJsonData)
                            buildRecyclerView()
                            recipesChanged = users.recipeJsonData
                        } else if (users.recipeJsonData == "") {
                            buildRecyclerView()
                        }
                    }
                })
            }
        }

        searchRecipeEditTextView = view.findViewById(R.id.search_recipe_edit_text_view)
        searchRecipeButton = view.findViewById(R.id.search_recipe_button)
        randomRecipeButton = view.findViewById(R.id.random_recipe_button)

        label = view.findViewById(R.id.select_label_text_view)
        label.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Select diet/health labels")
                    .setCancelable(false)
                    .setMultiChoiceItems(labelsArray, selectedLabels) { _, i, b ->
                        // When checkbox is selected
                        if (b) {
                            // Add position to labels list
                            labelsList.add(i)
                            labelsList.sort()
                        } else {
                            // When checkbox is unselected remove position from labels list
                            labelsList.remove(i)
                        }
                    }

                    .setPositiveButton("ok", DialogInterface.OnClickListener { _, _ ->
                        checkedLabels = StringBuilder()
                        val dietLabelsList = arrayListOf<String>()
                        val healthLabelsList = arrayListOf<String>()
                        for (j in 0 until labelsList.size) {
                            val choice = labelsArray[labelsList[j]]
                            checkedLabels.append(choice)
                            if (j != labelsList.size - 1) {
                                checkedLabels.append(", ")
                            }
                            if (choice == "balanced" || choice == "high-fiber" || choice == "high-protein" || choice == "low-carb" || choice == "low-fat" || choice == "low-sodium") {
                                dietLabelsList.add(choice)
                            } else {
                                healthLabelsList.add(choice)
                            }
                        }
                        label.text = checkedLabels.toString()
                        dietLabelUrl = dietLabelsList.toTypedArray()
                        healthLabelUrl = healthLabelsList.toTypedArray()
                        return@OnClickListener
                    })

                    .setNegativeButton("clear", DialogInterface.OnClickListener { _, _ ->
                        for (j in selectedLabels.indices) {
                            selectedLabels[j] = false
                        }
                        labelsList.clear()
                        checkedLabels = StringBuilder()
                        label.text = ""
                        dietLabelUrl = arrayOf()
                        healthLabelUrl = arrayOf()
                        return@OnClickListener
                    })

                builder.show()
            }
        })

        searchRecipeButton.setOnClickListener {
            val recipeName = searchRecipeEditTextView.text.toString().trim()
            if (recipeName.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a query in the search field",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                this.requireView().hideKeyboard()
                loadingDialog.startDialog()

                recipesList.clear()
                recipeRecyclerView.adapter?.notifyDataSetChanged()
                firebaseDatabaseViewModel.updateChildValues("recipeJsonData", "")

                edamamViewModel = ViewModelProvider(this)[EdamamViewModel::class.java]
                edamamViewModel.getEdamamRecipes(
                    "public",
                    false,
                    Constants.EDAMAM_API_ID,
                    Constants.EDAMAM_API_KEY,
                    false,
                    recipeName,
                    if (dietLabelUrl.isEmpty()) null else dietLabelUrl,
                    if (healthLabelUrl.isEmpty()) null else healthLabelUrl
                )

                edamamViewModel.getEdamamResultLiveData().observe(viewLifecycleOwner) {
                    if (it.hits!!.isEmpty()) {
                        loadingDialog.dismissDialog()
                        noCalculations("Sorry, we couldn't find any recipes for your search.")
                    } else {
                        for (i in it.hits.indices) {
                            // Calculate GHG emissions for the user's food
                            var score = BigDecimal("0.00")
                            // Get the list of ingredients for the recipe
                            val ingredients: List<ca.neunition.data.model.api.Ingredient>? =
                                it.hits[i].recipe?.ingredients
                            if (ingredients != null) {
                                for (ingredient in ingredients) {
                                    val re = Regex("[^a-zA-Z ]")
                                    var foodName =
                                        "${ingredient.text?.lowercase()} ${ingredient.foodCategory?.lowercase()}"
                                    foodName = re.replace(foodName, "")
                                    val mainFoods = foodName.split("\\s".toRegex())
                                    val foodWeight = ingredient.weight
                                    for (foodWord in mainFoods) {
                                        if (foodWord in foods) {
                                            score = score.add(
                                                BigDecimal(foods[foodWord]!!.toString()).multiply(
                                                    BigDecimal(foodWeight.toString())
                                                )
                                            )
                                            break
                                        }
                                    }
                                }
                            }
                            score = score.setScale(2, RoundingMode.HALF_UP)
                            val item = RecipeCard(
                                it.hits[i].recipe?.image,
                                it.hits[i].recipe?.label,
                                score,
                                it.hits[i].recipe?.url
                            )
                            recipesList += item
                            requireActivity().runOnUiThread(kotlinx.coroutines.Runnable {
                                run {
                                    recipeRecyclerView.adapter = adapter
                                    recipeRecyclerView.adapter?.notifyDataSetChanged()
                                }
                            })
                        }
                        lifecycleScope.launch(Dispatchers.Default) {
                            saveData()
                        }
                        loadingDialog.dismissDialog()
                    }
                }

                edamamViewModel.getStatusLiveData().observe(viewLifecycleOwner) {
                    loadingDialog.dismissDialog()
                    noCalculations(it)
                }
            }
        }

        randomRecipeButton.setOnClickListener {
            if (dietLabelUrl.isEmpty() && healthLabelUrl.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please select at least one diet/health label",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                this.requireView().hideKeyboard()
                loadingDialog.startDialog()

                searchRecipeEditTextView.setText("")
                recipesList.clear()
                recipeRecyclerView.adapter?.notifyDataSetChanged()
                firebaseDatabaseViewModel.updateChildValues("recipeJsonData", "")

                edamamViewModel = ViewModelProvider(this).get(EdamamViewModel::class.java)
                edamamViewModel.getEdamamRecipes(
                    "public",
                    false,
                    Constants.EDAMAM_API_ID,
                    Constants.EDAMAM_API_KEY,
                    true,
                    "",
                    if (dietLabelUrl.isEmpty()) null else dietLabelUrl,
                    if (healthLabelUrl.isEmpty()) null else healthLabelUrl
                )

                edamamViewModel.getEdamamResultLiveData().observe(viewLifecycleOwner) {
                    if (it.hits!!.isEmpty()) {
                        loadingDialog.dismissDialog()
                        noCalculations("Sorry, we couldn't find any recipes for your search.")
                    } else {
                        for (i in it.hits.indices) {
                            // Calculate GHG emissions for the user's food
                            var score = BigDecimal("0.00")
                            // Get the list of ingredients for the recipe
                            val ingredients: List<ca.neunition.data.model.api.Ingredient>? =
                                it.hits[i].recipe?.ingredients
                            if (ingredients != null) {
                                for (ingredient in ingredients) {
                                    val re = Regex("[^a-zA-Z ]")
                                    var foodName =
                                        "${ingredient.text?.lowercase()} ${ingredient.foodCategory?.lowercase()}"
                                    foodName = re.replace(foodName, "")
                                    val mainFoods = foodName.split("\\s".toRegex())
                                    val foodWeight = ingredient.weight
                                    for (foodWord in mainFoods) {
                                        if (foodWord in foods) {
                                            score = score.add(
                                                BigDecimal(foods[foodWord]!!.toString()).multiply(
                                                    BigDecimal(foodWeight.toString())
                                                )
                                            )
                                            break
                                        }
                                    }
                                }
                            }
                            score = score.setScale(2, RoundingMode.HALF_UP)
                            val item = RecipeCard(
                                it.hits[i].recipe?.image,
                                it.hits[i].recipe?.label,
                                score,
                                it.hits[i].recipe?.url
                            )
                            recipesList += item
                            requireActivity().runOnUiThread(kotlinx.coroutines.Runnable {
                                run {
                                    recipeRecyclerView.adapter = adapter
                                    recipeRecyclerView.adapter?.notifyDataSetChanged()
                                }
                            })
                        }
                        lifecycleScope.launch(Dispatchers.Default) {
                            saveData()
                        }
                        loadingDialog.dismissDialog()
                    }
                }

                edamamViewModel.getStatusLiveData().observe(viewLifecycleOwner) {
                    loadingDialog.dismissDialog()
                    noCalculations(it)
                }
            }
        }
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

    private fun saveData() {
        val moshi = Moshi.Builder().add(BigDecimalAdapter).add(KotlinJsonAdapterFactory()).build()
        val listMyData = Types.newParameterizedType(List::class.java, RecipeCard::class.java)
        val jsonAdapter: JsonAdapter<ArrayList<RecipeCard>> = moshi.adapter(listMyData)
        val json = jsonAdapter.toJson(recipesList)
        firebaseDatabaseViewModel.updateChildValues("recipeJsonData", json)
    }

    private fun loadData(json: String) {
        if (json != "") {
            val moshi =
                Moshi.Builder().add(BigDecimalAdapter).add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, RecipeCard::class.java)
            val jsonAdapter: JsonAdapter<ArrayList<RecipeCard>> = moshi.adapter(type)
            recipesList = jsonAdapter.fromJson(json)!!
        }
    }

    private fun buildRecyclerView() {
        adapter = RecipeAdapter(recipesList, this)
        recipeRecyclerView.layoutManager = recyclerViewLayoutManager
        recipeRecyclerView.adapter = adapter
    }
}
