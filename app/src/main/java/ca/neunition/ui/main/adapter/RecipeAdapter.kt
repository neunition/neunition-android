/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Adapter for showing a list of recipes with name, picture, and GHG emissions based on the user input.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import ca.neunition.R
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.util.recipeCardScore
import ca.neunition.util.spannableFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeAdapter(
    private val recipeList: List<RecipeCard>,
    private val listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    inner class RecipeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val recipeImageView: AppCompatImageView = itemView.findViewById(R.id.recipe_picture_image_view)
        val recipeTitleView: AppCompatTextView = itemView.findViewById(R.id.recipe_title_text_view)
        val recipeScoreView: AppCompatTextView = itemView.findViewById(R.id.recipe_ghg_score_text_view)

        init {
            recipeTitleView.setSpannableFactory(spannableFactory)
            recipeScoreView.setSpannableFactory(spannableFactory)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onRecipeClick(position)
            }
        }
    }

    interface OnRecipeClickListener {
        fun onRecipeClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_card, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipeList[position]

        Glide.get(holder.itemView.context).clearMemory()
        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(holder.itemView.context).clearDiskCache()
        }
        Glide.with(holder.itemView.context)
            .load(currentRecipe.recipeImage)
            .error(R.drawable.ic_baseline_error)
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(holder.recipeImageView)

        holder.recipeImageView.setColorFilter(
            Color.rgb(123, 123, 123),
            PorterDuff.Mode.MULTIPLY
        )

        holder.recipeTitleView.setText(
            SpannableString(currentRecipe.recipeTitle),
            TextView.BufferType.SPANNABLE
        )

        holder.recipeScoreView.setText(
            recipeCardScore(holder.itemView.context, currentRecipe.recipeScore),
            TextView.BufferType.SPANNABLE
        )
    }

    override fun getItemCount() = recipeList.size
}
