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
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import net.oleg.fd.MainActivity
import net.oleg.fd.R
import net.oleg.fd.ui.theme.FoodDiaryTheme
import net.oleg.fd.ui.theme.deleteButtonColors
import net.oleg.fd.ui.theme.invertedButtonColors
import net.oleg.fd.viewmodel.FloatFieldState
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditFoodForm(
    navController: NavHostController,
    viewModel: FoodViewModel,
) {
    val buttonPaddings = PaddingValues(start = 32.dp, end = 32.dp, top = 4.dp, bottom = 8.dp)

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val currentScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val (snackbarMessage, setSnackbarMessage) = remember { mutableStateOf(R.string.message_internal_error) }
    val (snackbarMessageArg, setSnackbarMessageArg) = remember { mutableStateOf<String?>(null) }
    val (isError, setError) = remember { mutableStateOf(false) }
    val (undoId, setUndoId) = remember { mutableStateOf<Long?>(null) }

    fun showMessage(@StringRes id: Int, arg: String? = null, isError: Boolean = false, undoId: Long? = null) =
        currentScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            setError(isError)
            setSnackbarMessage(id)
            setSnackbarMessageArg(arg)
            setUndoId(undoId)
            snackbarHostState.showSnackbar(message = "", duration = SnackbarDuration.Long)
        }

    fun showErrorMessage(@StringRes id: Int, arg: String? = null) =
        showMessage(id, arg, isError = true)

    // FIXME add tests for all useful variants
    // region need tests
    val foodData by viewModel.foodData.observeAsState()
    val barcode = foodData?.barcode
    val id = foodData?.id

    val foodItem = when {
        id != null -> viewModel.getFoodAsLiveData(id).observeAsState().value
        barcode != null -> viewModel.getFoodAsLiveData(barcode).observeAsState().value
        else -> null
    }

    // FIXME use viewmodel.setNNN
    val name = (foodData?.name ?: foodItem?.name) ?: ""
    val energy = foodData?.energy ?: FloatFieldState(foodItem?.energy)
    val carbs = foodData?.carbs ?: FloatFieldState(foodItem?.carbs)
    val fat = foodData?.fat ?: FloatFieldState(foodItem?.fat)
    val protein = foodData?.protein ?: FloatFieldState(foodItem?.protein)
    // endregion need tests

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (foodItem?.id != null) stringResource(R.string.header_edit_food) else stringResource(R.string.header_new_food),
                style = typography.headlineLarge
            )
            Text(
                text = if (barcode != null) stringResource(
                    R.string.header_with_barcode,
                    barcode
                ) else stringResource(R.string.header_no_barcode),
                style = typography.bodyMedium
            )
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = stringResource(R.string.header_units_100g),
                style = typography.bodySmall,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { viewModel.setFoodName(it) },
                label = { Text(stringResource(R.string.label_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                textStyle = typography.bodyLarge,
                shape = shapes.small
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .padding(end = 4.dp),
                    value = energy.value,
                    onValueChange = { value -> viewModel.setFoodEnergy(FloatFieldState(value)) },
                    label = { Text(stringResource(R.string.label_calories)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = energy.isError,
                    shape = shapes.small
                )
                OutlinedTextField(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .padding(start = 4.dp),
                    value = carbs.value,
                    onValueChange = { value -> viewModel.setFoodCarbs(FloatFieldState(value)) },
                    label = { Text(stringResource(R.string.label_carbs)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = carbs.isError,
                    shape = shapes.small
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .padding(end = 4.dp),
                    value = fat.value,
                    onValueChange = { value -> viewModel.setFoodFat(FloatFieldState(value)) },
                    label = { Text(stringResource(R.string.label_fat)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = fat.isError,
                    shape = shapes.small
                )
                OutlinedTextField(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .padding(start = 4.dp),
                    value = protein.value,
                    onValueChange = { value -> viewModel.setFoodProtein(FloatFieldState(value)) },
                    label = { Text(stringResource(R.string.label_protein)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    isError = protein.isError,
                    shape = shapes.small
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = buttonPaddings),
                onClick = {
                    when {
                        name.isEmpty() || energy.value.isEmpty() || carbs.value.isEmpty() ||
                                fat.value.isEmpty() || protein.value.isEmpty() ->
                            showErrorMessage(R.string.message_fill_form_and_save_again)

                        energy.isError || carbs.isError || fat.isError || protein.isError ->
                            showErrorMessage(R.string.message_correct_error_and_save_again)

                        else ->
                            currentScope.launch {
                                try {
                                    viewModel.insertOrUpdateFood(
                                        foodItem?.id,
                                        name,
                                        barcode,
                                        energy.toFloat(),
                                        carbs.toFloat(),
                                        fat.toFloat(),
                                        protein.toFloat()
                                    )
                                    showMessage(R.string.message_saved)
                                } catch (ex: Throwable) {
                                    showErrorMessage(R.string.message_database_error, ex.message)
                                }
                            }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.button_save))
            }

            Button(
                colors = invertedButtonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = buttonPaddings),
                onClick = {
                    viewModel.clearFoodData()
                    showMessage(R.string.message_cleared)
                }
            ) {
                Text(text = stringResource(id = R.string.button_clear_all))
            }

            Button(
                colors = invertedButtonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = buttonPaddings),
                onClick = {
                    viewModel.setCameraReturnPath(Screen.EditFood)
                    navController.navigate(Screen.Camera)
                }
            ) {
                Icon(
                    modifier = Modifier.padding(end = 4.dp),
                    imageVector = Icons.Filled.Camera,
                    contentDescription = stringResource(id = R.string.button_scan_barcode)
                )
                Text(text = stringResource(id = R.string.button_scan_barcode))
            }

            if (foodItem?.id != null) {
                Button(
                    colors = deleteButtonColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = buttonPaddings),
                    onClick = {
                        currentScope.launch {
                            viewModel.markFoodItemAsDeleted(foodItem.id)
                            viewModel.clearFoodData()
                            showMessage(R.string.message_deleted, undoId = foodItem.id)
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.button_delete)
                    )
                    Text(text = stringResource(id = R.string.button_delete))
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(color = colorScheme.inverseOnSurface, shape = shapes.small),
            ) {
                SideEffect {
                    keyboardController?.hide()
                }
                val text = if (snackbarMessageArg != null) {
                    stringResource(id = snackbarMessage, snackbarMessageArg)
                } else {
                    stringResource(id = snackbarMessage)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(12.dp),
                        text = text,
                        style = typography.bodyMedium,
                        color = if (isError) colorScheme.error else colorScheme.primary
                    )
                    if (undoId != null) {
                        Button(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(horizontal = 4.dp),
                            onClick = {
                                currentScope.launch {
                                    viewModel.markFoodItemAsNotDeleted(undoId).join()
                                    val restoredFoodItem = viewModel.getFood(undoId)
                                    viewModel.setFoodData(restoredFoodItem!!)
                                    showMessage(R.string.message_restored)
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.button_undo))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun FoodFormDefaultPreview() {
    FoodDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                EditFoodForm(NavHostController(MainActivity()), FoodViewModelMock)
            }
        )
    }
}