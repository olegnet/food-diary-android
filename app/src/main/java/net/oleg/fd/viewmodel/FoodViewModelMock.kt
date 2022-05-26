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
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Job
import net.oleg.fd.room.FoodDiaryItem
import net.oleg.fd.room.FoodDiarySum
import net.oleg.fd.room.FoodDiaryView
import net.oleg.fd.room.FoodItem
import net.oleg.fd.ui.Screen
import java.util.*

object FoodViewModelMock : FoodViewModel {

    override val calendar = MutableLiveData(Calendar.getInstance())
    override fun addDay(day: Int) {}

    override val selectedFoodDiaryView: LiveData<FoodDiaryView?>
        get() = MutableLiveData(
            FoodDiaryView(
                foodDiaryItem = FoodDiaryItem(
                    id = 1,
                    date = Date(),
                    itemId = 1,
                    weight = 20f
                ),
                foodItem = FoodItem(
                    id = 1,
                    name = "Very very long line so it should be second line here",
                    barcode = 4000417932006.toString(),
                    date = Date(1652262140799),
                    energy = 10.34f,
                    carbs = 2.0f,
                    fat = 3.0f,
                    protein = 4.0f,
                    itemIsDeleted = false
                )
            )
        )

    override fun setSelectedFoodDiaryView(foodDiaryView: FoodDiaryView?) {}

    override val foodDataRequest = MutableLiveData<FoodDataRequest?>(null)
    override fun setSearchString(search: String?) {}
    override fun setSearchBarcode(barcode: String?) {}

    override val selectedFoodItem: LiveData<FoodItem?>
        get() = MutableLiveData(
            FoodItem(
                id = 1,
                name = "Very very long line so it should be second line here",
                barcode = (4000417932006).toString(),
                date = Date(1652262140799),
                energy = 10.34f,
                carbs = 2.0f,
                fat = 3.0f,
                protein = 4.0f,
                itemIsDeleted = false
            )
        )

    override fun setSelectedFoodItem(foodData: FoodItem?) {}

    override val foodData = MutableLiveData<FoodData?>(null)
    override fun setFoodBarcodeAndClear(barcode: String?) {}
    override fun clearFoodData() {}
    override fun postFoodDataId(id: Long) {}

    override fun setFoodName(name: String) {}
    override fun setFoodEnergy(energy: FloatFieldState) {}
    override fun setFoodCarbs(carbs: FloatFieldState) {}
    override fun setFoodFat(fat: FloatFieldState) {}
    override fun setFoodProtein(protein: FloatFieldState) {}
    override fun setFoodData(foodItem: FoodItem) {}

    override val cameraReturnPath = MutableLiveData<Screen?>(null)
    override fun setCameraReturnPath(screen: Screen?) {}

    override suspend fun insertOrUpdateFood(
        id: Long?,
        name: String,
        barcode: String?,
        energy: Float,
        carbs: Float?,
        fat: Float?,
        protein: Float?,
    ) = Job()

    override suspend fun markFoodItemAsDeleted(id: Long) = Job()
    override fun getFoodItems(foodDataRequest: FoodDataRequest?): PagingSource<Int, FoodItem> =
        object : PagingSource<Int, FoodItem>() {
            override fun getRefreshKey(state: PagingState<Int, FoodItem>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodItem> =
                LoadResult.Page(mockFoodItemsList(), null, null)
        }

    override fun getAnyFoodItem() = MutableLiveData<FoodItem>()
    override fun getFoodAsLiveData(barcode: String) = MutableLiveData<FoodItem>()
    override fun getFoodAsLiveData(id: Long) = MutableLiveData<FoodItem>()
    override suspend fun getFood(barcode: String): FoodItem? = null

    override fun getFoodDiarySum(calendar: Calendar) = MutableLiveData(FoodDiarySum(1f, 2f, 3f, 4f))
    override fun getFoodDiary(calendar: Calendar): PagingSource<Int, FoodDiaryView> =
        object : PagingSource<Int, FoodDiaryView>() {
            override fun getRefreshKey(state: PagingState<Int, FoodDiaryView>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodDiaryView> =
                LoadResult.Invalid()
        }

    override fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem) = Job()
    override fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem) = Job()
    override fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem) = Job()

    // FIXME show it in preview
    private fun mockFoodItemsList(): List<FoodItem> =
        mutableListOf<FoodItem>().apply {
            add(selectedFoodItem.value!!)
            for (i in 2..50) {
                add(
                    FoodItem(
                        id = i.toLong(),
                        name = "Random food $i",
                        barcode = (4000417932006 + i).toString(),
                        date = Date(1652262140799 + i),
                        energy = 50.0f + i,
                        carbs = 100.0f - i,
                        fat = 30.0f + i * 2,
                        protein = 180.0f - i * 2,
                        itemIsDeleted = false
                    )
                )
            }
        }
}