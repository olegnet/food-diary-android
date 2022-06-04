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

import android.icu.text.DateFormat
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.oleg.fd.R
import net.oleg.fd.databinding.FragmentDailyListBinding
import net.oleg.fd.room.FoodDiaryView
import net.oleg.fd.viewModel
import net.oleg.fd.viewmodel.FoodViewModel
import java.util.*

class DailyListFragment : Fragment() {

    private lateinit var viewModel: FoodViewModel

    private var selectedFoodDiaryView: FoodDiaryView? = null

    private var _binding: FragmentDailyListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity.viewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDailyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DailyListAdapter { _, foodDiaryView ->
            if (foodDiaryView == selectedFoodDiaryView) {
                viewModel.setSelectedFoodDiaryView(null)
            } else {
                viewModel.setSelectedFoodDiaryView(foodDiaryView)
            }
        }

        val recyclerView = binding.dailyListRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            showHeaderDate(calendar.time)

            viewModel.getFoodDiarySum(calendar).observe(viewLifecycleOwner) { dailySums ->
                // FIXME binding.dailySums = dailySums
            }

            lifecycleScope.launch {
                viewModel.getFoodDiary(calendar).collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        viewModel.selectedFoodDiaryView.observe(viewLifecycleOwner) { foodDiaryView ->
            selectedFoodDiaryView = foodDiaryView
            // FIXME
        }

        binding.buttonDecreaseDay.setOnClickListener { viewModel.addDay(day = -1) }
        binding.buttonIncreaseDay.setOnClickListener { viewModel.addDay(day = 1) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showHeaderDate(date: Date) {
        val header = when {
            isToday(date) -> R.string.header_today
            isYesterday(date) -> R.string.header_yesterday
            isTomorrow(date) -> R.string.header_tomorrow
            else -> null
        }
        if (header != null) {
            binding.headerText.text = resources.getText(header)
            binding.headerText.visibility = View.VISIBLE
        } else {
            binding.headerText.visibility = View.GONE
        }
        binding.dateText.text = formatDate(date)
    }

    private fun formatDate(date: Date): String =
        DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_DAY)
            .format(date)

    private fun isToday(date: Date): Boolean =
        DateUtils.isToday(date.time)

    private fun isYesterday(date: Date): Boolean =
        DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS)

    private fun isTomorrow(date: Date): Boolean =
        DateUtils.isToday(date.time - DateUtils.DAY_IN_MILLIS)
}