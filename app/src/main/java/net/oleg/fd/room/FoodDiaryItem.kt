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

import androidx.room.*
import java.util.*

@Entity(
    tableName = "food_diary",
    foreignKeys = [
        ForeignKey(entity = FoodItem::class, parentColumns = ["item_id"], childColumns = ["diary_item_id"])
    ],
    indices = [
        Index(value = ["diary_item_id"]),
        Index(value = ["diary_date"]),
    ]
)
data class FoodDiaryItem(
    @ColumnInfo(name = "diary_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long?,

    @ColumnInfo(name = "diary_date", defaultValue = "CURRENT_TIMESTAMP")
    val date: Date?,

    @ColumnInfo(name = "diary_item_id", typeAffinity = ColumnInfo.INTEGER)
    val itemId: Long,

    @ColumnInfo(name = "diary_weight", typeAffinity = ColumnInfo.REAL)
    val weight: Float,
)