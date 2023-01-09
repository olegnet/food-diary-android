/*
 * Copyright 2022-2023 Oleg Okhotnikov
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

package net.oleg.fd.json

import androidx.annotation.RawRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.oleg.fd.MainActivity
import net.oleg.fd.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Serializable
data class FoodDataJson(
    @SerialName("data") val data: List<FoodData>,
)

@Serializable
data class FoodData(
    @SerialName("name") val name: String?,
    @SerialName("energy") val energy: Float?,
    @SerialName("carbs") val carbs: Float?,
    @SerialName("fat") val fat: Float?,
    @SerialName("protein") val protein: Float?,
)

@OptIn(ExperimentalSerializationApi::class)
@RunWith(AndroidJUnit4::class)
class ImportJsonTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun openRawResource(@RawRes id: Int) = composeTestRule.activity.resources.openRawResource(id)

    @Test
    fun check_json_test() {
        val json = openRawResource(R.raw.nutrition)
        val foodDataJson = Json.decodeFromStream<FoodDataJson>(json)

        assertEquals(9943, foodDataJson.data.size)

        foodDataJson.data.forEach {
            assertNotNull(it.name)
            assertNotNull(it.energy)
            assertNotNull(it.protein)
            assertNotNull(it.fat)
            assertNotNull(it.carbs)
        }
    }
}