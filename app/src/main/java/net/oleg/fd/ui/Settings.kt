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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.oleg.fd.R
import net.oleg.fd.ui.theme.FoodDiaryTheme
import net.oleg.fd.ui.theme.invertedButtonColors
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock

@Composable
fun Settings(
    viewModel: FoodViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.nav_bar_settings),
                style = typography.headlineSmall
            )
        }

        ImportNutritionData(viewModel)
    }
}

@Composable
fun ImportNutritionData(
    viewModel: FoodViewModel,
) {
    val resources = LocalContext.current.resources
    val currentScope = rememberCoroutineScope()
    val isNutritionDataImported by viewModel.isNutritionDataImported.observeAsState()
    val importNutritionDataProgress by viewModel.importNutritionDataProgress.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp, vertical = 12.dp)
        Button(
            modifier = modifier,
            colors = invertedButtonColors(),
            enabled = isNutritionDataImported != true && importNutritionDataProgress == null,
            onClick = {
                currentScope.launch {
                    val nutrition = resources.openRawResource(R.raw.nutrition)
                    viewModel.importNutritionData(nutrition)
                }
            }
        ) {
            Text(
                text = stringResource(
                    id = if (isNutritionDataImported != true)
                        R.string.button_import_food_data
                    else
                        R.string.message_food_data_imported
                )
            )
        }
        if (importNutritionDataProgress != null) {
            LinearProgressIndicator(
                modifier = modifier,
                progress = importNutritionDataProgress ?: 0f
            )
        } else {
            LinearProgressIndicator(
                modifier = modifier,
                color = colorScheme.background,
                progress = 1f
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun SettingsDefaultPreview() {
    FoodDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                Settings(FoodViewModelMock)
            }
        )
    }
}