/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Base class that serves as the application-level dependency container for Hilt's code generation.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NeunitionApplication : Application()
