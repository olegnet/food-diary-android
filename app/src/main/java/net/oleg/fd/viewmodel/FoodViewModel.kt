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

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import kotlinx.coroutines.Job
import net.oleg.fd.room.FoodDiaryItem
import net.oleg.fd.room.FoodDiarySum
import net.oleg.fd.room.FoodDiaryView
import net.oleg.fd.room.FoodItem
import net.oleg.fd.ui.Screen
import java.io.InputStream
import java.util.*

interface FoodViewModel {

    // Main screen header date
    val calendar: LiveData<Calendar>
    fun addDay(day: Int)

    // Selected item in the List on Main screen
    val selectedFoodDiaryView: LiveData<FoodDiaryView?>
    fun setSelectedFoodDiaryView(foodDiaryView: FoodDiaryView?)

    // Search string in Add Food Screen
    val foodDataRequest: LiveData<FoodDataRequest?>
    fun setSearchString(search: String?)
    fun setSearchBarcode(barcode: String?)

    // Selected item in the List of Food Items
    val selectedFoodItem: LiveData<FoodItem?>
    fun setSelectedFoodItem(foodData: FoodItem?)

    // Edit food screen
    val foodData: LiveData<FoodData?>
    fun setFoodBarcodeAndClear(barcode: String?)
    fun clearFoodData()
    fun postFoodDataId(id: Long)
    fun setFoodName(name: String)
    fun setFoodEnergy(energy: FloatFieldState)
    fun setFoodCarbs(carbs: FloatFieldState)
    fun setFoodFat(fat: FloatFieldState)
    fun setFoodProtein(protein: FloatFieldState)
    fun setFoodData(foodItem: FoodItem)

    // Camera
    // FIXME don't use Screen in ViewModel
    val cameraReturnPath: LiveData<Screen?>
    fun setCameraReturnPath(screen: Screen?)

    // Import json
    val isNutritionDataImported: LiveData<Boolean>
    val importNutritionDataProgress: LiveData<Float?>
    fun importNutritionData(inputStream: InputStream)

    // Database
    suspend fun insertOrUpdateFood(
        id: Long?,
        name: String,
        barcode: String?,
        energy: Float,
        carbs: Float?,
        fat: Float?,
        protein: Float?,
    ): Job
    suspend fun markFoodItemAsDeleted(id: Long): Job
    suspend fun markFoodItemAsNotDeleted(id: Long): Job
    fun getFoodItems(foodDataRequest: FoodDataRequest?): PagingSource<Int, FoodItem>
    fun getAnyFoodItem(): LiveData<FoodItem>
    fun getFoodAsLiveData(id: Long): LiveData<FoodItem>
    fun getFoodAsLiveData(barcode: String): LiveData<FoodItem>
    suspend fun getFood(id: Long): FoodItem?
    suspend fun getFood(barcode: String): FoodItem?
    fun getFoodDiarySum(calendar: Calendar): LiveData<FoodDiarySum>
    fun getFoodDiary(calendar: Calendar): PagingSource<Int, FoodDiaryView>
    fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem): Job
    fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem): Job
    fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem): Job
}