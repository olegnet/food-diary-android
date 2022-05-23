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