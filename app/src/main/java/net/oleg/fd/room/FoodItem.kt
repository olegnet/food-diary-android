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

package net.oleg.fd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "food_item",
    indices = [
        Index(value = ["name"]),
        Index(value = ["barcode"]),
        Index(value = ["item_date"]),
        Index(value = ["item_is_deleted"]),
    ]
)
data class FoodItem(
    @ColumnInfo(name = "item_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long?,

    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.UNICODE)
    val name: String,

    @ColumnInfo(name = "barcode", typeAffinity = ColumnInfo.UNICODE)
    val barcode: String?,

    @ColumnInfo(name = "item_date", defaultValue = "CURRENT_TIMESTAMP")
    val date: Date?,

    @ColumnInfo(name = "energy", typeAffinity = ColumnInfo.REAL)
    val energy: Float,

    @ColumnInfo(name = "carbs", typeAffinity = ColumnInfo.REAL)
    val carbs: Float?,

    @ColumnInfo(name = "fat", typeAffinity = ColumnInfo.REAL)
    val fat: Float?,

    @ColumnInfo(name = "protein", typeAffinity = ColumnInfo.REAL)
    val protein: Float?,

    @ColumnInfo(name = "item_is_deleted")
    val itemIsDeleted: Boolean,
)