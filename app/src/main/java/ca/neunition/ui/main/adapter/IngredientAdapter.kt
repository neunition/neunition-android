/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Adapter for showing the list of ingredients with their carbon emissions score in a scrollable list.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import ca.neunition.R
import ca.neunition.data.remote.response.IngredientCard
import ca.neunition.util.spannableFactory

class IngredientAdapter(
    private val ingredientsList: List<IngredientCard>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        return IngredientViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentIngredient = ingredientsList[position]
        holder.apply {
            ingredientCalcTextView.setText(
                ingredientCardText(currentIngredient.ingredientText, currentIngredient.italicizeText),
                TextView.BufferType.SPANNABLE
            )

            deleteIngredientImageView.contentDescription = "Remove ${currentIngredient.ingredientText}"
        }
    }

    override fun getItemCount() = ingredientsList.size

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val ingredientCalcTextView: AppCompatTextView =
            itemView.findViewById(R.id.calculation_of_ingredient_text_view)
        val deleteIngredientImageView: AppCompatImageView =
            itemView.findViewById(R.id.ingredient_delete)

        init {
            ingredientCalcTextView.setSpannableFactory(spannableFactory)
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                ingredientCalcTextView,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
            deleteIngredientImageView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = absoluteAdapterPosition
            if (view == deleteIngredientImageView && position != RecyclerView.NO_POSITION) {
                onClickListener.onDeleteClick(position)
            }
        }
    }

    interface OnClickListener {
        fun onDeleteClick(position: Int)
    }

    /**
     * The text to place inside of a card that will be added to the ingredients' emissions list.
     *
     * @param ingredientText The raw text
     * @param italicText Italicize the text or not
     *
     * @return The text that has been correctly formatted to be displayed in the card.
     */
    private fun ingredientCardText(ingredientText: String, italicText: Boolean): SpannableString {
        val ingredientTextSpannable = SpannableString(ingredientText)

        if (italicText) {
            ingredientTextSpannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                0,
                ingredientText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return ingredientTextSpannable
    }
}
