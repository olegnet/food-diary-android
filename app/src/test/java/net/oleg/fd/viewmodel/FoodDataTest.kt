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

package net.oleg.fd.viewmodel

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])                     // FIXME
open class FoodDataTest {

    @Test
    fun foodDataBuilder_FirstConstructor_Test() {
        val foodData = FoodData.Builder()
            .id(1)
            .unitId(2)
            .name("name")
            .barcode("barcode")
            .energy(FloatFieldState(1.2f))
            .carbs(FloatFieldState(3.4f))
            .fat(FloatFieldState(5.6f))
            .protein(FloatFieldState(7.8f))
            .build()
        assertEquals(1L, foodData.id)
        assertEquals(2L, foodData.unitId)
        assertEquals("name", foodData.name)
        assertEquals("barcode", foodData.barcode)
        assertEquals("1.2", foodData.energy!!.value)
        assertEquals("3.4", foodData.carbs!!.value)
        assertEquals("5.6", foodData.fat!!.value)
        assertEquals("7.8", foodData.protein!!.value)
    }

    @Test
    fun foodDataBuilder_SecondConstructor_Test() {
        val foodDataFirst = FoodData.Builder()
            .id(1)
            .unitId(2)
            .name("name")
            .barcode("barcode")
            .energy(FloatFieldState(1.2f))
            .carbs(FloatFieldState(3.4f))
            .fat(FloatFieldState(5.6f))
            .protein(FloatFieldState(7.8f))
            .build()

        val foodData = FoodData.Builder(foodDataFirst)
            .id(3)
            .unitId(4)
            .name("name 5")
            .barcode("barcode 6")
            .energy(FloatFieldState(7.8f))
            .carbs(FloatFieldState(9.10f))
            .fat(FloatFieldState(11.12f))
            .protein(FloatFieldState(13.14f))
            .build()
        assertEquals(3L, foodData.id)
        assertEquals(4L, foodData.unitId)
        assertEquals("name 5", foodData.name)
        assertEquals("barcode 6", foodData.barcode)
        assertEquals("7.8", foodData.energy!!.value)
        assertEquals("9.1", foodData.carbs!!.value)
        assertEquals("11.12", foodData.fat!!.value)
        assertEquals("13.14", foodData.protein!!.value)
    }
}