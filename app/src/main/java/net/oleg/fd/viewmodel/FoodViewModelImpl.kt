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

package net.oleg.fd.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.oleg.fd.json.FoodDataJson
import net.oleg.fd.preferences.DataStoreRepository
import net.oleg.fd.room.*
import net.oleg.fd.ui.Screen
import timber.log.Timber
import java.io.InputStream
import java.util.*

class FoodViewModelImpl(
    private val roomRepository: FoodRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel(), FoodViewModel {

    // region calendar
    private val _calendar = MutableLiveData(Calendar.getInstance())
    override val calendar: LiveData<Calendar> = _calendar

    override fun addDay(day: Int) {
        val calendar = _calendar.value!!.clone() as Calendar
        calendar.add(Calendar.DAY_OF_MONTH, day)
        _calendar.value = calendar
    }
    // endregion calendar

    // region selectedFoodDiaryItem
    private val _selectedFoodDiaryView = MutableLiveData<FoodDiaryView?>(null)
    override val selectedFoodDiaryView: LiveData<FoodDiaryView?>
        get() = _selectedFoodDiaryView

    override fun setSelectedFoodDiaryView(foodDiaryView: FoodDiaryView?) {
        _selectedFoodDiaryView.value = foodDiaryView
    }
    // endregion selectedFoodDiaryItem

    // region searchString
    private val _foodDataRequest = MutableLiveData<FoodDataRequest?>(null)
    override val foodDataRequest: LiveData<FoodDataRequest?> = _foodDataRequest

    override fun setSearchString(search: String?) {
        _foodDataRequest.value = FoodDataRequest.Builder(_foodDataRequest.value)
            .search(search)
            .build()
    }

    override fun setSearchBarcode(barcode: String?) {
        _foodDataRequest.value = FoodDataRequest.Builder(_foodDataRequest.value)
            .barcode(barcode)
            .build()
    }
    // endregion searchString

    // region selectedFoodItem
    private val _selectedFoodItem = MutableLiveData<FoodItem?>(null)
    override val selectedFoodItem: LiveData<FoodItem?> = _selectedFoodItem

    override fun setSelectedFoodItem(foodData: FoodItem?) {
        _selectedFoodItem.value = foodData
    }
    // endregion selectedFoodItem

    // region foodData
    private val _foodData = MutableLiveData<FoodData?>(null)
    override val foodData: LiveData<FoodData?> = _foodData

    override fun setFoodBarcodeAndClear(barcode: String?) {
        _foodData.value = FoodData(barcode = barcode)
    }

    override fun clearFoodData() {
        _foodData.value = FoodData()
    }

    override fun postFoodDataId(id: Long) {
        _foodData.postValue(
            FoodData.Builder(_foodData.value)
                .id(id)
                .build()
        )
    }

    override fun setFoodName(name: String) {
        _foodData.value = FoodData.Builder(_foodData.value)
            .name(name)
            .build()
    }

    override fun setFoodEnergy(energy: FloatFieldState) {
        _foodData.value = FoodData.Builder(_foodData.value)
            .energy(energy)
            .build()
    }

    override fun setFoodCarbs(carbs: FloatFieldState) {
        _foodData.value = FoodData.Builder(_foodData.value)
            .carbs(carbs)
            .build()
    }

    override fun setFoodFat(fat: FloatFieldState) {
        _foodData.value = FoodData.Builder(_foodData.value)
            .fat(fat)
            .build()
    }

    override fun setFoodProtein(protein: FloatFieldState) {
        _foodData.value = FoodData.Builder(_foodData.value)
            .protein(protein)
            .build()
    }

    override fun setFoodData(foodItem: FoodItem) {
        _foodData.value = FoodData(
            id = foodItem.id,
            name = foodItem.name,
            barcode = foodItem.barcode,
            energy = FloatFieldState(foodItem.energy),
            carbs = FloatFieldState(foodItem.carbs),
            fat = FloatFieldState(foodItem.fat),
            protein = FloatFieldState(foodItem.protein),
        )
    }
    // endregion foodData

    // region camera
    private val _cameraReturnPath = MutableLiveData<Screen?>(null)
    override val cameraReturnPath: LiveData<Screen?> = _cameraReturnPath

    override fun setCameraReturnPath(screen: Screen?) {
        _cameraReturnPath.value = screen
    }
    // endregion camera

    // region nutritionData
    override val isNutritionDataImported: LiveData<Boolean>
        get() = dataStoreRepository.isNutritionDataImportedFlow.asLiveData()

    private val _importNutritionDataProgress = MutableLiveData<Float?>(null)
    override val importNutritionDataProgress: LiveData<Float?>
        get() = _importNutritionDataProgress

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun importNutritionData(inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            var count = 0
            _importNutritionDataProgress.postValue(0f)

            val foodDataJson = Json.decodeFromStream<FoodDataJson>(inputStream)
            Timber.d("importNutritionData: size: ${foodDataJson.data.size}")

            val foodItems = mutableListOf<FoodItem>()
            val date = Date()
            foodDataJson.data.forEach {
                count++
                if (count % 200 == 0) {
                    _importNutritionDataProgress.postValue(count.toFloat() / foodDataJson.data.size)
                }
                val insertFoodItem = FoodItem(
                    id = null,
                    name = it.name,
                    barcode = null,
                    date = date,
                    energy = it.energy,
                    carbs = it.carbs,
                    fat = it.fat,
                    protein = it.protein,
                    itemIsDeleted = false
                )
                foodItems.add(insertFoodItem)
            }

            val result = roomRepository.insertFoodItems(foodItems)
            Timber.d("importNutritionData: result.size: ${result.size}")

            Timber.d("importNutritionData: time: ${Date().time - date.time}")

            if (result.size != foodDataJson.data.size) {
                // FIXME show message instead
                throw RuntimeException("json data size: source=${foodDataJson.data.size} inserted=${result.size}")
            }

            dataStoreRepository.setNutritionDataImported()

            _importNutritionDataProgress.postValue(null)
        }
    }
    // endregion nutritionData

    // region database
    override suspend fun insertOrUpdateFood(
        id: Long?,
        name: String,
        barcode: String?,
        energy: Float,
        carbs: Float?,
        fat: Float?,
        protein: Float?,
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (id != null) {
            val foodItem = roomRepository.getFood(id)
            if (foodItem?.name == name &&
                foodItem.barcode == barcode &&
                foodItem.energy == energy &&
                foodItem.carbs == carbs &&
                foodItem.fat == fat &&
                foodItem.protein == protein &&
                !foodItem.itemIsDeleted
            ) {
                // No changes, skip it
                return@launch
            }
        }

        val insertFoodItem = FoodItem(
            id = null,
            name = name,
            barcode = barcode,
            date = Date(),
            energy = energy,
            carbs = carbs,
            fat = fat,
            protein = protein,
            itemIsDeleted = false
        )
        val newId = roomRepository.insertFoodItem(insertFoodItem)

        if (id != null) {
            roomRepository.markFoodItemAsDeleted(id)
        }

        // If next time user press Save button with no changes we will skip it above
        // see "no changes" comment
        postFoodDataId(newId)
    }

    override suspend fun markFoodItemAsDeleted(id: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.markFoodItemAsDeleted(id)
        }

    override suspend fun markFoodItemAsNotDeleted(id: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.markFoodItemAsNotDeleted(id)
        }

    override fun getFoodItems(foodDataRequest: FoodDataRequest?): PagingSource<Int, FoodItem> =
        when {
            foodDataRequest?.search != null ->
                roomRepository.getFoodItems(foodDataRequest.search)
            foodDataRequest?.barcode != null ->
                roomRepository.getFoodItemsByBarcode(foodDataRequest.barcode)
            else ->
                roomRepository.getAllFoodItems()
        }

    override fun getAnyFoodItem(): LiveData<FoodItem> =
        roomRepository.getAnyFoodItem().asLiveData()

    override fun getFoodAsLiveData(barcode: String): LiveData<FoodItem> =
        roomRepository.getFoodForLivedata(barcode).asLiveData()

    override fun getFoodAsLiveData(id: Long): LiveData<FoodItem> =
        roomRepository.getFoodForLivedata(id).asLiveData()

    override suspend fun getFood(barcode: String): FoodItem? =
        roomRepository.getFood(barcode)

    override suspend fun getFood(id: Long): FoodItem? =
        roomRepository.getFood(id)

    override fun getFoodDiarySum(calendar: Calendar): LiveData<FoodDiarySum> =
        roomRepository.getFoodDiarySum(getStartOfDay(calendar), getEndOfDay(calendar)).asLiveData()

    override fun getFoodDiary(calendar: Calendar): PagingSource<Int, FoodDiaryView> =
        roomRepository.getFoodDiary(getStartOfDay(calendar), getEndOfDay(calendar))

    override fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.insertFoodDiaryItem(foodDiaryItem)
            roomRepository.updateFoodItemDate(foodDiaryItem.itemId)
        }

    override fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.updateFoodDiaryItem(foodDiaryItem)
            roomRepository.updateFoodItemDate(foodDiaryItem.itemId)
        }

    override fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.deleteFoodDiaryItem(foodDiaryItem)
        }
    // endregion database

    // region utils
    private fun getStartOfDay(calendar: Calendar): Date {
        val clone = calendar.clone() as Calendar
        clone.set(Calendar.HOUR_OF_DAY, 0)
        clone.set(Calendar.MINUTE, 0)
        clone.set(Calendar.SECOND, 0)
        clone.set(Calendar.MILLISECOND, 0)
        return clone.time
    }

    private fun getEndOfDay(calendar: Calendar): Date {
        val clone = calendar.clone() as Calendar
        clone.set(Calendar.HOUR_OF_DAY, 23)
        clone.set(Calendar.MINUTE, 59)
        clone.set(Calendar.SECOND, 59)
        clone.set(Calendar.MILLISECOND, 999)
        return clone.time
    }
    // endregion utils
}