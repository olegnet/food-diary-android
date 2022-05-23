package net.oleg.fd.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.oleg.fd.room.*
import net.oleg.fd.ui.Screen
import java.util.*

class FoodViewModelImpl(
    private val repository: FoodRepository,
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

    private val _cameraReturnPath = MutableLiveData<Screen?>(null)
    override val cameraReturnPath: LiveData<Screen?> = _cameraReturnPath

    override fun setCameraReturnPath(screen: Screen?) {
        _cameraReturnPath.value = screen
    }
    // endregion foodData

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
            val foodItem = repository.getFood(id)
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
        val newId = repository.insertFoodItem(insertFoodItem)

        if (id != null) {
            repository.markFoodItemAsDeleted(id)
        }

        // If next time user press Save button with no changes we will skip it above
        // see "no changes" comment
        postFoodDataId(newId)
    }

    override suspend fun markFoodItemAsDeleted(id: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.markFoodItemAsDeleted(id)
        }

    override fun getFoodItems(foodDataRequest: FoodDataRequest?): PagingSource<Int, FoodItem> =
        when {
            foodDataRequest?.search != null ->
                repository.getFoodItems(foodDataRequest.search)
            foodDataRequest?.barcode != null ->
                repository.getFoodItemsByBarcode(foodDataRequest.barcode)
            else ->
                repository.getAllFoodItems()
        }

    override fun getAnyFoodItem(): LiveData<FoodItem> =
        repository.getAnyFoodItem().asLiveData()

    override fun getFoodAsLiveData(barcode: String): LiveData<FoodItem> =
        repository.getFoodForLivedata(barcode).asLiveData()

    override fun getFoodAsLiveData(id: Long): LiveData<FoodItem> =
        repository.getFoodForLivedata(id).asLiveData()

    override suspend fun getFood(barcode: String): FoodItem? =
        repository.getFood(barcode)

    override fun getFoodDiarySum(calendar: Calendar): LiveData<FoodDiarySum> =
        repository.getFoodDiarySum(getStartOfDay(calendar), getEndOfDay(calendar)).asLiveData()

    override fun getFoodDiary(calendar: Calendar): PagingSource<Int, FoodDiaryView> =
        repository.getFoodDiary(getStartOfDay(calendar), getEndOfDay(calendar))

    override fun insertFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFoodDiaryItem(foodDiaryItem)
            repository.updateFoodItemDate(foodDiaryItem.itemId)
        }

    override fun updateFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFoodDiaryItem(foodDiaryItem)
            repository.updateFoodItemDate(foodDiaryItem.itemId)
        }

    override fun deleteFoodDiaryItem(foodDiaryItem: FoodDiaryItem) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFoodDiaryItem(foodDiaryItem)
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