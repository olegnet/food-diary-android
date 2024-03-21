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

package net.oleg.fd.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingConfig
import net.oleg.fd.room.FoodItem

val pagingConfig =
    PagingConfig(
        pageSize = 20,
        enablePlaceholders = true,
        maxSize = 200
    )

fun LazyListScope.lazyColumnProgressItem(isInLoadingState: Boolean) {
    val modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
    item {
        if (isInLoadingState) {
            LinearProgressIndicator(
                modifier = modifier
            )
        } else {
            LinearProgressIndicator(
                modifier = modifier,
                trackColor = Color.Transparent,
                progress = 0f
            )
        }
    }
}

val foodItemDummy = FoodItem(
    id = 0,
    name = "",
    barcode = null,
    date = null,
    energy = 0f,
    carbs = 0f,
    fat = 0f,
    protein = 0f,
    itemIsDeleted = false
)
