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

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.oleg.fd.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun stringResource(@StringRes id: Int) = composeTestRule.activity.getString(id)

    @Test
    fun mainActivityScreen_clickTo_Edit_Test() {
        listOf(Screen.DailyList, Screen.AddToDailyList, Screen.EditFood, Screen.Camera).forEach {
            composeTestRule.onNodeWithContentDescription(stringResource(it.text))
                .assertExists()
                .assertHasClickAction()
        }

        composeTestRule.onNodeWithContentDescription(stringResource(Screen.EditFood.text))
            .performClick()

        composeTestRule.onNodeWithText("New food")
            .assertExists()
    }
}