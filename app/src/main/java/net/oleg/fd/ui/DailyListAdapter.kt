/*
 * Copyright 2022 Oleg Okhotnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oleg.fd.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.oleg.fd.R
import net.oleg.fd.room.FoodDiaryView
import net.oleg.fd.ui.DailyListAdapter.DailyListViewHolder
import net.oleg.fd.viewmodel.divider

class DailyListAdapter(
    private val onItemClicked: (position: Int, foodDiaryView: FoodDiaryView) -> Unit,
) : PagingDataAdapter<FoodDiaryView, DailyListViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyListViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_list_recyclerview_item, parent, false)
        return DailyListViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: DailyListViewHolder, position: Int) {
        val current = getItem(position)!!   // FIXME !!
        holder.bind(current)
    }

    class DailyListViewHolder(
        itemView: View,
        private val onItemClicked: (position: Int, foodDiaryView: FoodDiaryView) -> Unit,
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val nameItemView: TextView = itemView.findViewById(R.id.recyclerview_food_diary_name)
        private val weightItemView: TextView = itemView.findViewById(R.id.recyclerview_food_diary_weight)
        private val energyItemView: TextView = itemView.findViewById(R.id.recyclerview_food_diary_energy)

        private lateinit var _foodDiaryView: FoodDiaryView

        fun bind(foodDiaryView: FoodDiaryView) {
            val foodItem = foodDiaryView.foodItem
            val weight = foodDiaryView.foodDiaryItem.weight
            val energy = weight * foodItem.energy / divider

            val context = itemView.context
            nameItemView.text = foodItem.name
            weightItemView.text = context.getString(R.string.units_weight, weight.printToForm())
            energyItemView.text = context.getString(R.string.units_energy, energy.printToForm())
            _foodDiaryView = foodDiaryView
        }

        override fun onClick(v: View) {
            onItemClicked(bindingAdapterPosition, _foodDiaryView)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FoodDiaryView>() {
            override fun areItemsTheSame(oldItem: FoodDiaryView, newItem: FoodDiaryView): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FoodDiaryView, newItem: FoodDiaryView): Boolean {
                return oldItem.foodItem.id == newItem.foodItem.id &&
                        oldItem.foodDiaryItem.id == newItem.foodDiaryItem.id &&
                        oldItem.foodDiaryItem.weight == newItem.foodDiaryItem.weight
            }
        }
    }
}
