package net.oleg.fd.viewmodel

import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Test

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

    @Test
    fun floatFieldStateTest() {
        assertEquals(FloatFieldState("a"), FloatFieldState("a", true))
        assertEquals(FloatFieldState(1.1f), FloatFieldState("1.1", false))

        assertTrue(FloatFieldState(0f).isError)
        assertTrue(FloatFieldState(-1f).isError)
        assertFalse(FloatFieldState(1f).isError)
    }
}