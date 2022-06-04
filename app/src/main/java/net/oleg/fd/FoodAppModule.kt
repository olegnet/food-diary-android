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

package net.oleg.fd

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.oleg.fd.room.FoodDao
import net.oleg.fd.room.FoodDatabase
import net.oleg.fd.room.FoodRepository
import net.oleg.fd.viewmodel.FoodViewModelFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FoodAppModule {

    @Provides
    fun provideFoodDao(database: FoodDatabase): FoodDao {
        return database.foodDao()
    }

    @Provides
    fun provideFoodRepository(foodDao: FoodDao): FoodRepository {
        return FoodRepository(foodDao)
    }

    @Provides
    @Singleton
    fun provideNotesRoomDatabase(@ApplicationContext context: Context): FoodDatabase {
        return FoodDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFoodViewModelFactory(repository: FoodRepository): FoodViewModelFactory {
        return FoodViewModelFactory(repository)
    }
}