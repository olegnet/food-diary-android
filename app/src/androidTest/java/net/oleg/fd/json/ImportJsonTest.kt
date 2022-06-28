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

package net.oleg.fd.json

import androidx.annotation.RawRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.oleg.fd.MainActivity
import net.oleg.fd.R
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalSerializationApi::class)
@RunWith(AndroidJUnit4::class)
class ImportJsonTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun openRawResource(@RawRes id: Int) = composeTestRule.activity.resources.openRawResource(id)

    @Test
    fun check_json_test() {
        val json = openRawResource(R.raw.energy)
        val foodDataJson = Json.decodeFromStream<FoodDataJson>(json)
        Assert.assertEquals(13467, foodDataJson.data.size)
    }
}