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

package net.oleg.fd.room

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FoodDao {

    @RawQuery(observedEntities = [FoodItem::class])
    fun getFoodItems(query: SupportSQLiteQuery): PagingSource<Int, FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "ORDER BY item_date DESC"
    )
    fun getAllFoodItems(): PagingSource<Int, FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND food_item.barcode = :barcode"
    )
    fun getFoodItemsByBarcode(barcode: String): PagingSource<Int, FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "LIMIT 1"
    )
    fun getAnyFoodItem(): Flow<FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND item_id = :id"
    )
    fun getFoodItem(id: Long): Flow<FoodItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFoodItems(foodItems: List<FoodItem>): List<Long>

    @Query("UPDATE food_item " +
            "SET item_is_deleted = 1 " +
            "WHERE item_id = :id"
    )
    suspend fun markFoodItemAsDeleted(id: Long)

    @Query("UPDATE food_item " +
            "SET item_is_deleted = 0 " +
            "WHERE item_id = :id"
    )
    suspend fun markFoodItemAsNotDeleted(id: Long)

    @Query("UPDATE food_item " +
            "SET item_date = CURRENT_TIMESTAMP " +
            "WHERE item_id = :id"
    )
    suspend fun updateFoodItemDate(id: Long)

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND food_item.barcode = :barcode"
    )
    fun getFoodForLivedata(barcode: String): Flow<FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND food_item.barcode = :barcode"
    )
    suspend fun getFood(barcode: String): FoodItem?

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND food_item.item_id = :id"
    )
    fun getFoodForLivedata(id: Long): Flow<FoodItem>

    @Query("SELECT * " +
            "FROM food_item " +
            "WHERE item_is_deleted = 0 " +
            "AND food_item.item_id = :id"
    )
    suspend fun getFood(id: Long): FoodItem?

    @Query(
        "SELECT COUNT(*) AS count, SUM(food_item.energy * food_diary.diary_weight) AS energy, " +
                "SUM(food_item.carbs * food_diary.diary_weight) AS carbs, " +
                "SUM(food_item.fat * food_diary.diary_weight) AS fat, " +
                "SUM(food_item.protein * food_diary.diary_weight) AS protein " +
                "FROM food_item " +
                "INNER JOIN food_diary ON food_diary.diary_item_id = food_item.item_id " +
                "WHERE food_diary.diary_date BETWEEN :startDate AND :endDate"
    )
    fun getFoodDiarySum(startDate: Date, endDate: Date): Flow<FoodDiarySum>

    @Query(
        "SELECT food_diary.diary_id, food_item.*, food_diary.* " +
                "FROM food_item " +
                "INNER JOIN food_diary ON food_diary.diary_item_id = food_item.item_id " +
                "WHERE food_diary.diary_date BETWEEN :startDate AND :endDate " +
                "ORDER BY food_diary.diary_date DESC"
    )
    fun getFoodDiary(startDate: Date, endDate: Date): PagingSource<Int, FoodDiaryView>

    @Query("SELECT * " +
            "FROM food_diary " +
            "WHERE diary_id = :id"
    )
    fun getFoodDiaryItem(id: Long): Flow<FoodDiaryItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem)

    @Delete
    suspend fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem)
}