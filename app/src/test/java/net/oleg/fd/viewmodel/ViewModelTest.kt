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

package net.oleg.fd.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.oleg.fd.room.FoodDao
import net.oleg.fd.room.FoodRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
open class ViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var foodDao: FoodDao

    private lateinit var viewModel: FoodViewModel

    @Before
    fun startUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = FoodViewModelImpl(FoodRepository(foodDao))
    }

    @Test
    fun initialValuesTest() {
        assertNotNull(viewModel)
        assertNotNull(viewModel.foodData)
        assertNull(viewModel.foodData.value)
        assertNotNull(viewModel.calendar)
        assertNotNull(viewModel.calendar.value)
    }

    @Test
    fun foodDataTest() {
        viewModel.setFoodBarcodeAndClear("test barcode")
        assertEquals(viewModel.foodData.value, FoodData(barcode = "test barcode"))

        viewModel.setFoodName("test name")
        assertEquals(viewModel.foodData.value, FoodData(name = "test name", barcode = "test barcode"))

        viewModel.setFoodEnergy(FloatFieldState(1.2f))
        assertEquals(
            viewModel.foodData.value,
            FoodData(
                name = "test name",
                barcode = "test barcode",
                energy = FloatFieldState("1.2"),
            )
        )

        viewModel.setFoodCarbs(FloatFieldState(2.3f))
        assertEquals(
            viewModel.foodData.value,
            FoodData(
                name = "test name",
                barcode = "test barcode",
                energy = FloatFieldState("1.2"),
                carbs = FloatFieldState("2.3"),
            )
        )

        viewModel.setFoodFat(FloatFieldState(3.4f))
        assertEquals(
            viewModel.foodData.value,
            FoodData(
                name = "test name",
                barcode = "test barcode",
                energy = FloatFieldState("1.2"),
                carbs = FloatFieldState("2.3"),
                fat = FloatFieldState("3.4"),
            )
        )

        viewModel.setFoodProtein(FloatFieldState(4.5f))
        assertEquals(
            viewModel.foodData.value,
            FoodData(
                name = "test name",
                barcode = "test barcode",
                energy = FloatFieldState("1.2", false),
                carbs = FloatFieldState("2.3", false),
                fat = FloatFieldState("3.4", false),
                protein = FloatFieldState("4.5", false),
            )
        )

        viewModel.postFoodDataId(1L)
        assertEquals(
            viewModel.foodData.value,
            FoodData(
                id = 1L,
                name = "test name",
                barcode = "test barcode",
                energy = FloatFieldState("1.2", false),
                carbs = FloatFieldState("2.3", false),
                fat = FloatFieldState("3.4", false),
                protein = FloatFieldState("4.5", false),
            )
        )

        viewModel.setFoodBarcodeAndClear("test barcode 2")
        assertEquals(viewModel.foodData.value, FoodData(barcode = "test barcode 2"))

        viewModel.clearFoodData()
        assertEquals(viewModel.foodData.value, FoodData())
    }
}