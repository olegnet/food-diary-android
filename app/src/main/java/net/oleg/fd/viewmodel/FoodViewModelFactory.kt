package net.oleg.fd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.oleg.fd.room.FoodRepository

class FoodViewModelFactory(
    private val repository: FoodRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModelImpl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModelImpl(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}