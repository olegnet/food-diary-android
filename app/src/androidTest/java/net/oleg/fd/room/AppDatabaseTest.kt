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

package net.oleg.fd.room

import android.content.Context
import android.text.format.DateUtils
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var foodDao: FoodDao
    private lateinit var database: FoodDatabase

    private val scope = TestScope()

    @Before
    fun createDb() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))

        val context: Context = ApplicationProvider.getApplicationContext()
        runBlocking {
            database = FoodDatabase.getDatabase(context)
            foodDao = database.foodDao()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    @Ignore(value = "Run it manually")
    @Throws(Exception::class)
    fun addDefaultData() = runTest {
        val foodItemList = listOf(
            FoodItem(null, "quadratisch", "4000417932006", Date(1652262140799), 1.0f, 2.0f, 3.0f, 4.0f, false),
            FoodItem(null, "quadratisch 2", "4000417212009", Date(1652270512503), 5.0f, 6.0f, 7.0f, 8.0f, false),
        )
        val newFoodItemList = mutableListOf<FoodItem>()
        foodItemList.forEach {
            val id = foodDao.insertFoodItem(it)
            newFoodItemList.add(it.replaceId(id))
        }
        newFoodItemList.forEach { foodItem ->
            assertNotNull(foodItem.id)
            flow<FoodItem> {
                foodDao.getFoodItem(foodItem.id!!)
            }.collect {
                assertEquals(foodItem, it)
            }
        }
    }

    @Test
    @Ignore(value = "Run it manually")
    @Throws(Exception::class)
    fun addGeneratedData() = runTest {
        val foodItemList = mutableListOf<FoodItem>()
        val foodDiaryItemList = mutableListOf<FoodDiaryItem>()

        for (i in 1..49) {
            val item = FoodItem(
                id = null,
                name = "Random food $i",
                barcode = (4000417932006 + i).toString(),
                date = Date(),
                energy = 50.0f + i,
                carbs = 100.0f - i,
                fat = 30.0f + i * 2,
                protein = 180.0f - i * 2,
                itemIsDeleted = false
            )
            val itemId = foodDao.insertFoodItem(item)
            foodItemList.add(item.replaceId(itemId))

            val foodDiaryItem = FoodDiaryItem(
                id = null,
                date = if (itemId % 2 == 0L) Date() else Date(Date().time - DateUtils.DAY_IN_MILLIS),
                itemId = itemId,
                weight = Random().nextFloat()
            )
            val diaryItemId = foodDao.insertFoodDiaryItem(foodDiaryItem)
            foodDiaryItemList.add(foodDiaryItem.replaceId(diaryItemId))
        }

        val item50 = FoodItem(
            id = null,
            name = "Very very long line so it should be second line here and then even more text",
            barcode = 4000417932006.toString(),
            date = Date(),
            energy = 10.34f,
            carbs = 2.0f,
            fat = 3.0f,
            protein = 4.0f,
            itemIsDeleted = false
        )
        val id50 = foodDao.insertFoodItem(item50)
        foodItemList.add(item50.replaceId(id50))

        foodItemList.forEach { foodItem ->
            assertNotNull(foodItem.id)
            flow<FoodItem> {
                foodDao.getFoodItem(foodItem.id!!)
            }.collect {
                assertEquals(foodItem, it)
            }
        }

        foodDiaryItemList.forEach { foodDiaryItem ->
            assertNotNull(foodDiaryItem.id)
            flow<FoodItem> {
                foodDao.getFoodDiaryItem(foodDiaryItem.id!!)
            }.collect {
                assertEquals(foodDiaryItem, it)
            }
        }
    }

    @Test
    @Ignore(value = "Will clear all data in the app")
    @Throws(Exception::class)
    fun clearAllTables() = runTest {
        database.clearAllTables()
    }
}