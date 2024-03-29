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

import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.util.*

class FoodRepository(
    private val foodDao: FoodDao
) {
    private val spaces = Regex("\\s+")

    @WorkerThread
    fun getFoodItems(search: String): PagingSource<Int, FoodItem> {
        val args = search.split(spaces)
        val subQuery = args.joinToString(" ") { _ -> "AND name LIKE '%' || ? || '%'" }
        val query = "SELECT * FROM food_item WHERE item_is_deleted = 0 $subQuery ORDER BY item_date DESC"
        return foodDao.getFoodItems(SimpleSQLiteQuery(query, args.toTypedArray()))
    }

    @WorkerThread
    fun getAllFoodItems(): PagingSource<Int, FoodItem> =
        foodDao.getAllFoodItems()

    @WorkerThread
    fun getFoodItemsByBarcode(search: String): PagingSource<Int, FoodItem> =
        foodDao.getFoodItemsByBarcode(search)

    @WorkerThread
    fun getAnyFoodItem(): Flow<FoodItem> =
        foodDao.getAnyFoodItem()

    suspend fun insertFoodItem(foodItem: FoodItem): Long =
        foodDao.insertFoodItem(foodItem)

    suspend fun insertFoodItems(foodItems: List<FoodItem>): List<Long> =
        foodDao.insertFoodItems(foodItems)

    suspend fun markFoodItemAsDeleted(id: Long) {
        foodDao.markFoodItemAsDeleted(id)
    }

    suspend fun markFoodItemAsNotDeleted(id: Long) {
        foodDao.markFoodItemAsNotDeleted(id)
    }

    suspend fun updateFoodItemDate(id: Long) {
        foodDao.updateFoodItemDate(id)
    }

    @WorkerThread
    fun getFoodForLivedata(barcode: String): Flow<FoodItem> =
        foodDao.getFoodForLivedata(barcode)

    suspend fun getFood(barcode: String): FoodItem? =
        foodDao.getFood(barcode)

    @WorkerThread
    fun getFoodForLivedata(id: Long): Flow<FoodItem> =
        foodDao.getFoodForLivedata(id)

    suspend fun getFood(id: Long): FoodItem? =
        foodDao.getFood(id)

    @WorkerThread
    fun getFoodDiarySum(startDate: Date, endDate: Date): Flow<FoodDiarySum> =
        foodDao.getFoodDiarySum(startDate, endDate)

    @WorkerThread
    fun getFoodDiary(startDate: Date, endDate: Date): PagingSource<Int, FoodDiaryView> =
        foodDao.getFoodDiary(startDate, endDate)

    suspend fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem): Long =
        foodDao.insertFoodDiaryItem(foodDiaryItem)

    suspend fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem) {
        foodDao.updateFoodDiaryItem(foodDiaryItem)
    }

    suspend fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem) {
        foodDao.deleteFoodDiaryItem(foodDiaryItem)
    }
}