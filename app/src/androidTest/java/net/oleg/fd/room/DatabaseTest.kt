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