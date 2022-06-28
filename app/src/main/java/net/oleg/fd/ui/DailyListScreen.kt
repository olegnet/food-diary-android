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

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import net.oleg.fd.MainActivity
import net.oleg.fd.R
import net.oleg.fd.room.FoodDiaryItem
import net.oleg.fd.ui.theme.FoodDiaryTheme
import net.oleg.fd.ui.theme.invertedButtonColors
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock
import net.oleg.fd.viewmodel.divider
import java.util.*

@Composable
fun DailyListScreen(
    navController: NavHostController,
    viewModel: FoodViewModel,
) {
    val calendar by viewModel.calendar.observeAsState(Calendar.getInstance())
    val dailySums by viewModel.getFoodDiarySum(calendar).observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        DailyListScreenHeader(calendar) { viewModel.addDay(day = it) }

        NutritionInfo(
            (dailySums?.energy ?: 0f) / divider,
            (dailySums?.carbs ?: 0f) / divider,
            (dailySums?.fat ?: 0f) / divider,
            (dailySums?.protein ?: 0f) / divider
        )

        if (dailySums?.count == 0L) {
            val anyFood by viewModel.getAnyFoodItem().observeAsState()

            NoFoodColumn(
                viewModel = viewModel,
                message = R.string.message_no_food_added_yet,
                onAddButtonClick = {
                    if (anyFood == null) {
                        navController.navigate(Screen.EditFood)
                    } else {
                        navController.navigate(Screen.AddToDailyList)
                    }
                }
            )
        } else {
            DailyListScreenBody(viewModel)
        }
    }
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

@Composable
private fun DailyListScreenHeader(
    calendar: Calendar,
    addDay: (Int) -> Unit,
) {
    val header = when {
        isToday(date = calendar.time) -> R.string.header_today
        isYesterday(date = calendar.time) -> R.string.header_yesterday
        isTomorrow(date = calendar.time) -> R.string.header_tomorrow
        else -> null
    }
    val date = formatDate(date = calendar.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.Start)
                .padding(end = 4.dp),
            colors = invertedButtonColors(),
            onClick = { addDay(-1) }
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.header_arrow_back))
        }
        Column(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (header != null) {
                Text(text = stringResource(header), style = typography.headlineSmall)
                Text(text = date, style = typography.bodySmall)
            } else {
                Text(text = date, style = typography.headlineSmall)
            }
        }
        Button(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.End)
                .padding(start = 4.dp),
            colors = invertedButtonColors(),
            onClick = { addDay(1) }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = stringResource(R.string.header_arrow_forward)
            )
        }
    }
}

@Composable
private fun DailyListScreenBody(
    viewModel: FoodViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val calendar by viewModel.calendar.observeAsState(Calendar.getInstance())
    val selectedFoodDiaryView by viewModel.selectedFoodDiaryView.observeAsState()

    val pager = remember {
        Pager(
            config = pagingConfig,
            pagingSourceFactory = { viewModel.getFoodDiary(calendar) }
        )
    }
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    val observer = { _: Calendar ->
        lazyPagingItems.refresh()
    }
    viewModel.calendar.observe(lifecycleOwner, observer)
    // FIXME do we need removeObserver here?
    DisposableEffect(viewModel.calendar, lifecycleOwner) {
        onDispose {
            viewModel.calendar.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        lazyColumnProgressItem(isInLoadingState = lazyPagingItems.loadState.refresh == LoadState.Loading)

        itemsIndexed(lazyPagingItems) { _, foodDiaryView ->
            // TODO foodDiaryView can be null if scrolling is too fast
            val foodItem = foodDiaryView?.foodItem ?: foodItemDummy
            val weight = foodDiaryView?.foodDiaryItem?.weight
            val energy = if (weight != null) {
                weight * foodItem.energy / divider
            } else null

            Row(
                modifier = Modifier
                    .height(72.dp)      // FIXME line height(16.sp) x 2
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
                    .background(
                        shape = shapes.small,
                        brush = SolidColor(colorScheme.inverseOnSurface),
                        alpha = 0.5f
                    )
                    .clickable {
                        if (foodDiaryView == selectedFoodDiaryView) {
                            viewModel.setSelectedFoodDiaryView(null)
                        } else {
                            viewModel.setSelectedFoodDiaryView(foodDiaryView)
                        }
                    },
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    text = foodItem.name,
                    style = if (foodDiaryView == selectedFoodDiaryView) typography.titleMedium else typography.bodyLarge,
                    maxLines = 2
                )
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(1f)
                            .height(0.dp),
                        text = if (weight != null) stringResource(
                            id = R.string.units_weight,
                            weight.printToForm()
                        ) else "",
                        style = typography.labelLarge
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(1f)
                            .height(0.dp),
                        text = if (energy != null) stringResource(
                            id = R.string.units_energy,
                            energy.printToForm()
                        ) else "",
                        style = typography.labelLarge
                    )
                }
            }

            if (foodDiaryView != null && foodDiaryView == selectedFoodDiaryView) {
                val selectedFoodDiaryItem = selectedFoodDiaryView!!.foodDiaryItem
                EditFoodWeightRow(
                    foodItem = foodItem,
                    weight = selectedFoodDiaryItem.weight,
                    secondaryActionButton = {
                        DeleteIconButton {
                            // FIXME Add "are you sure?" question or snackbar with undo
                            viewModel.setSelectedFoodItem(null)
                            viewModel.deleteFoodDiaryItem(selectedFoodDiaryItem)
                        }
                    },
                    onClick = { _weight ->
                        val foodDiaryItem = FoodDiaryItem(
                            id = selectedFoodDiaryItem.id,
                            date = selectedFoodDiaryItem.date,
                            itemId = selectedFoodDiaryItem.itemId,
                            weight = _weight
                        )
                        viewModel.updateFoodDiaryItem(foodDiaryItem)
                        viewModel.setSelectedFoodItem(null)
                        // FIXME add Snackbar
                    }
                )
            }
        }

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun DailyListScreenDefaultPreview() {
    FoodDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                DailyListScreen(NavHostController(MainActivity()), FoodViewModelMock)
            }
        )
    }
}