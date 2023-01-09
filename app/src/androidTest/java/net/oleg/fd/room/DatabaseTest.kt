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

package net.oleg.fd.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var foodDao: FoodDao
    private lateinit var database: FoodDatabase

    private val scope = TestScope()

    @Before
    fun createDb() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))

        val context: Context = ApplicationProvider.getApplicationContext()
        runBlocking {
            database = Room.inMemoryDatabaseBuilder(context, FoodDatabase::class.java)
                .allowMainThreadQueries()
                .build()
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
    @Throws(Exception::class)
    fun testPrepopulatedTables() = runTest {
        // FIXME
//        val itemUnit = foodDao.getFoodItemUnit(defaultFoodItemUnit.unitId)
//        assertEquals(defaultFoodItemUnit, itemUnit)
    }
}