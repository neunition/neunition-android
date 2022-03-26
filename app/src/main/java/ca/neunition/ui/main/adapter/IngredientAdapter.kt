/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Adapter for showing the list of ingredients with their carbon emissions score in a scrollable list.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.adapter

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
import ca.neunition.util.ingredientCardText
import ca.neunition.util.spannableFactory

class IngredientAdapter(
    private val ingredientsList: List<IngredientCard>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_card, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentIngredient = ingredientsList[position]
        holder.ingredientCalcTextView.setText(
            ingredientCardText(currentIngredient.ingredientText, currentIngredient.italicizeText),
            TextView.BufferType.SPANNABLE
        )
    }

    override fun getItemCount() = ingredientsList.size

    inner class IngredientViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val ingredientCalcTextView: AppCompatTextView =
            itemView.findViewById(R.id.calculation_of_ingredient_text_view)
        private val deleteIngredientImageView: AppCompatImageView =
            itemView.findViewById(R.id.ingredient_delete)

        init {
            ingredientCalcTextView.setSpannableFactory(spannableFactory)
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                ingredientCalcTextView,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
            deleteIngredientImageView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onDeleteClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
    }
}
