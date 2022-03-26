/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * The data to be used to create and identify users.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.response

data class Users(
    var fullName: String? = "",
    val daily: Double = 0.0,
    val weekly: Double = 0.0,
    val monthly: Double = 0.0,
    val yearly: Double = 0.0,
    val previousRecords: String = "",
    val profileImageUrl: String = "",
    val recipeJsonData: String = ""
)
