/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * The data to be used to create and identify users.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.remote.response

import com.google.firebase.database.Exclude
import java.io.Serializable

data class User(
    var fullName: String? = "",
    val daily: Double = 0.0,
    val weekly: Double = 0.0,
    val monthly: Double = 0.0,
    val yearly: Double = 0.0,
    val previousRecords: String = "",
    val profileImageUrl: String = "",
    val recipesJsonData: String = "",
    @get:Exclude var isAuthenticated: Boolean = false,
    @get:Exclude var isNew: Boolean = false,
    @get:Exclude var isCreated: Boolean = false,
) : Serializable
