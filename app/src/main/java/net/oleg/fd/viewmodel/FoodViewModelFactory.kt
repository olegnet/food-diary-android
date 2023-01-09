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

package net.oleg.fd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.oleg.fd.preferences.DataStoreRepository
import net.oleg.fd.room.FoodRepository

class FoodViewModelFactory(
    private val roomRepository: FoodRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModelImpl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModelImpl(roomRepository, dataStoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}