/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * The data to be used for creating items in the ingredient's list.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.response

import java.math.BigDecimal

data class IngredientCard(
    val ingredientText: String,
    val weight: BigDecimal,
    val italicizeText: Boolean
)
