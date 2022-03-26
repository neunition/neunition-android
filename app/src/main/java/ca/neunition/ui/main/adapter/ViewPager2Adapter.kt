/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * ViewPager2 adapter for paging through fragments (CarbonFoodCalc, Recipes, and IngredientsCalc).
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.neunition.ui.main.view.GreenhouseGasEmissionsFragment
import ca.neunition.ui.main.view.IngredientsEmissionsFragment
import ca.neunition.ui.main.view.RecipesFragment

class ViewPager2Adapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount() = NUM_TABS

    override fun createFragment(position: Int): Fragment = when (position) {
            0 -> GreenhouseGasEmissionsFragment()
            1 -> RecipesFragment()
            else -> IngredientsEmissionsFragment()
    }

    companion object {
        private const val NUM_TABS = 3
    }
}
