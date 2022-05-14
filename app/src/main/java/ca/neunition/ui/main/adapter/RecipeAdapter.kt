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
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.RecipeCard
import ca.neunition.util.spannableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class RecipeAdapter(
    private val recipesList: ArrayList<RecipeCard>,
    private val listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.recipe_card,
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipesList[position]

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

        holder.apply {
            recipeImageView.setColorFilter(
                Color.rgb(123, 123, 123),
                PorterDuff.Mode.MULTIPLY
            )

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

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val recipeImageView: AppCompatImageView =
            itemView.findViewById(R.id.recipe_picture_image_view)
        val recipeTitleView: AppCompatTextView = itemView.findViewById(R.id.recipe_title_text_view)
        val recipeScoreView: AppCompatTextView =
            itemView.findViewById(R.id.recipe_ghg_score_text_view)

        init {
            recipeTitleView.setSpannableFactory(spannableFactory)
            recipeScoreView.setSpannableFactory(spannableFactory)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onRecipeClick(position)
            }
        }
    }

    interface OnRecipeClickListener {
        fun onRecipeClick(position: Int)
    }

    private fun String?.trimRecipeTitle(): CharSequence = if (this!!.length >= 26) {
        "${this.subSequence(0, 26).trim()}..."
    } else {
        this.toString().trim()
    }

    private fun recipeCardScore(context: Context, score: BigDecimal): SpannableString {
        val scoreSpannable = SpannableString(score.toString())

        if (score <= BigDecimal("1.08")) {
            scoreSpannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.greenScore)),
                0,
                score.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (score > BigDecimal("1.08") && score <= BigDecimal("1.61")) {
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
