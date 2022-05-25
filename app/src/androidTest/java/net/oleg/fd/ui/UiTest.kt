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