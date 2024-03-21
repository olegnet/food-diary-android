/*
 * Copyright 2022-2024 Oleg Okhotnikov
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

package net.oleg.fd.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository(
    private val context: Context
) {
    private val _true = 1
    private val isNutritionDataImportedKey = intPreferencesKey("is_nutrition_data_imported")

    val isNutritionDataImportedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[isNutritionDataImportedKey] == _true
        }

    suspend fun setNutritionDataImported() {
        context.dataStore.edit { preferences ->
            preferences[isNutritionDataImportedKey] = _true
        }
    }
}