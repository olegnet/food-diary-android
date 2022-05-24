package net.oleg.fd.ui

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ClearAll
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import net.oleg.fd.ui.theme.requestBarcodeSpanStyle
import net.oleg.fd.viewmodel.FoodDataRequest
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock
import java.util.*

@Composable
fun AddToDailyListScreen(
    navController: NavHostController,
    viewModel: FoodViewModel
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val foodDataRequest by viewModel.foodDataRequest.observeAsState()
    val searchString = foodDataRequest?.search ?: ""
    val barcode = foodDataRequest?.barcode
    val selectedFoodItem by viewModel.selectedFoodItem.observeAsState()

    val pager = remember {
        Pager(
            config = pagingConfig,
            pagingSourceFactory = { viewModel.getFoodItems(foodDataRequest) }
        )
    }
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    val observer = { _: FoodDataRequest? ->
        lazyPagingItems.refresh()
    }
    viewModel.foodDataRequest.observe(lifecycleOwner, observer)
    // FIXME do we need removeObserver here?
    DisposableEffect(viewModel.foodDataRequest, lifecycleOwner) {
        onDispose {
            viewModel.foodDataRequest.removeObserver(observer)
        }
    }

    fun clearSearchAndSelected() {
        viewModel.setSearchBarcode(null)
        viewModel.setSearchString(null)
        viewModel.setSelectedFoodItem(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            val label = @Composable { Text(text = stringResource(id = R.string.label_search)) }
            val modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            val singleLine = true
            val textStyle = typography.bodyLarge
            val shape = shapes.small
            if (barcode != null) {
                OutlinedTextField(
                    modifier = modifier,
                    label = label,
                    singleLine = singleLine,
                    textStyle = textStyle,
                    shape = shape,
                    value = TextFieldValue(
                        annotatedString = AnnotatedString(text = barcode, spanStyle = requestBarcodeSpanStyle()),
                        composition = TextRange(start = 0, end = barcode.length)
                    ),
                    onValueChange = {
                        clearSearchAndSelected()
                        // FIXME keyboard behavior
                    },
                )
            } else {
                OutlinedTextField(
                    modifier = modifier,
                    label = label,
                    singleLine = singleLine,
                    textStyle = textStyle,
                    shape = shape,
                    value = searchString,
                    onValueChange = {
                        viewModel.setSearchBarcode(null)
                        viewModel.setSearchString(it)
                        viewModel.setSelectedFoodItem(null)
                    },
                )
            }
            IconButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 4.dp),
                onClick = {
                    // FIXME add the same action on first 'back' pressing
                    clearSearchAndSelected()
                    focusManager.clearFocus()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ClearAll,
                    contentDescription = stringResource(id = R.string.button_clear_all)
                )
            }
            IconButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 4.dp),
                onClick = {
                    viewModel.setCameraReturnPath(Screen.AddToDailyList)
                    navController.navigate(Screen.Camera)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Camera,
                    contentDescription = stringResource(id = R.string.button_scan_barcode)
                )
            }
        }

        if (lazyPagingItems.itemCount < 1) {
            NoFoodColumn(
                text = if (foodDataRequest?.barcode != null)
                    R.string.message_food_with_barcode_not_found
                else
                    R.string.message_no_food_added_yet,
                onClick = {
                    navController.navigate(Screen.EditFood)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                lazyColumnProgressItem(
                    lazyListScope = this,
                    isInLoadingState = lazyPagingItems.loadState.refresh == LoadState.Loading
                )

                itemsIndexed(lazyPagingItems) { _, foodItem ->
                    // TODO foodItem can be null if scrolling is too fast
                    val thisItemIsSelected = foodItem != null && foodItem == selectedFoodItem

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)      // FIXME line height(16.sp) x 2
                            .padding(top = 4.dp, bottom = 4.dp)
                            .background(
                                shape = shapes.small,
                                brush = SolidColor(colorScheme.inverseOnSurface),
                                alpha = 0.5f
                            )
                            .clickable {
                                if (thisItemIsSelected) {
                                    viewModel.setSelectedFoodItem(null)
                                } else {
                                    viewModel.setSelectedFoodItem(foodItem)
                                }
                            },
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(CenterVertically)
                                .padding(8.dp),
                            text = foodItem?.name ?: "",
                            style = if (thisItemIsSelected) typography.titleMedium else typography.bodyLarge,
                            maxLines = 2
                        )
                        Text(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(CenterVertically)
                                .padding(8.dp),
                            text = foodItem?.energy?.printToForm() ?: "",
                            style = typography.bodyLarge
                        )
                    }

                    if (thisItemIsSelected) {
                        val localFoodItem = selectedFoodItem!!
                        EditFoodWeightRow(
                            foodItem = localFoodItem,
                            secondaryActionButton = {
                                EditIconButton {
                                    viewModel.setFoodData(localFoodItem)
                                    clearSearchAndSelected()
                                    navController.navigate(Screen.EditFood)
                                }
                            },
                            onClick = { weight ->
                                val foodDiaryItem = FoodDiaryItem(
                                    id = null,
                                    date = makeDate(viewModel.calendar.value!!),
                                    itemId = localFoodItem.id!!,
                                    weight = weight
                                )
                                viewModel.insertFoodDiaryItem(foodDiaryItem)
                                clearSearchAndSelected()
                                navController.navigate(Screen.DailyList)
                                // FIXME add Snackbar in DailyList
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
    }
}

private fun makeDate(selectedDate: Calendar?): Date {
    val now = Date()
    val date = selectedDate?.time ?: now

    return if (DateUtils.isToday(date.time)) {
        now
    } else {
        // TODO return time in a day that placed new record later than all existed
        date
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun AddToDailyListScreenPreview() {
    FoodDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                AddToDailyListScreen(
                    navController = NavHostController(MainActivity()),
                    viewModel = FoodViewModelMock
                )
            }
        )
    }
}