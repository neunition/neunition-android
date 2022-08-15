/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Adapter for showing a list of recipes with name, picture, and GHG emissions based on the user
 * input.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.util.Constants
import ca.neunition.util.spannableFactory
import com.bumptech.glide.Glide
import java.math.BigDecimal

class RecipeCardAdapter(
    private val recipesList: ArrayList<RecipeCard>,
    private val onClickListener: OnClickListener,
) : RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
        return RecipeCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecipeCardViewHolder, position: Int) {
        val currentRecipe = recipesList[position]

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(currentRecipe.recipeImage)
            .error(R.drawable.ic_baseline_error)
            .apply(Constants.REQUEST_OPTIONS)
            .into(holder.recipeImageView)

        holder.apply {
            recipeTitleView.setText(
                SpannableString(currentRecipe.recipeTitle.trimRecipeTitle()),
                TextView.BufferType.SPANNABLE
            )

            recipeScoreView.setText(
                recipeCardScore(holder.itemView.context, currentRecipe.recipeScore),
                TextView.BufferType.SPANNABLE
            )
        }
    }

    override fun getItemCount() = recipesList.size

    inner class RecipeCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val recipeImageView: AppCompatImageView =
            itemView.findViewById(R.id.recipe_picture_image_view)
        val recipeTitleView: AppCompatTextView = itemView.findViewById(R.id.recipe_title_text_view)
        val recipeScoreView: AppCompatTextView =
            itemView.findViewById(R.id.recipe_ghg_score_text_view)
        private val recipeAddEmissionsButton: AppCompatImageButton =
            itemView.findViewById(R.id.recipe_add_emissions_button)

        init {
            recipeTitleView.setSpannableFactory(spannableFactory)
            recipeScoreView.setSpannableFactory(spannableFactory)
            itemView.setOnClickListener(this)
            recipeAddEmissionsButton.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = absoluteAdapterPosition
            if (view == itemView && position != RecyclerView.NO_POSITION) {
                onClickListener.onRecipeClick(position)
            } else if (view == recipeAddEmissionsButton && position != RecyclerView.NO_POSITION) {
                onClickListener.onAddEmissionsClick(position)
            }
        }
    }

    interface OnClickListener {
        fun onRecipeClick(position: Int)
        fun onAddEmissionsClick(position: Int)
    }

    private fun String?.trimRecipeTitle(): CharSequence = if (this!!.length >= 22) {
        "${this.subSequence(0, 22).trim()}..."
    } else {
        this.toString().trim()
    }

    private fun recipeCardScore(context: Context, score: BigDecimal): SpannableString {
        val scoreSpannable = SpannableString(score.toString())

        if (score <= BigDecimal("1.85")) {
            scoreSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.greenScore)),
                0,
                score.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (score > BigDecimal("1.85") && score <= BigDecimal("2.05")) {
            scoreSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellowScore)),
                0,
                score.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            scoreSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.redScore)),
                0,
                score.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return scoreSpannable
    }
}
