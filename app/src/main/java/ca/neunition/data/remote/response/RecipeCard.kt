/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * The data to be used for creating recipe cards.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.response

import java.math.BigDecimal

data class RecipeCard(
    val recipeImage: String?,
    val recipeTitle: String?,
    val recipeScore: BigDecimal,
    val recipeUrl: String?
)
