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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodDataRequest(
    val search: String?,
    val barcode: String?,
) : Parcelable {

    class Builder() {
        constructor(foodData: FoodDataRequest?) : this() {
            search = foodData?.search
            barcode = foodData?.barcode
        }

        private var search: String? = null
        private var barcode: String? = null

        fun search(search: String?) = apply { this.search = search }
        fun barcode(barcode: String?) = apply { this.barcode = barcode }

        fun build(): FoodDataRequest =
            FoodDataRequest(search, barcode)
    }
}