package net.oleg.fd.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val divider = 100   // per 100 gram TODO

@Parcelize
data class FoodData(
    val id: Long? = null,
    val unitId: Long? = null,
    val name: String? = null,
    val barcode: String? = null,
    val energy: FloatFieldState? = null,
    val carbs: FloatFieldState? = null,
    val fat: FloatFieldState? = null,
    val protein: FloatFieldState? = null,
) : Parcelable {

    class Builder() {
        constructor(foodData: FoodData?) : this() {
            id = foodData?.id
            unitId = foodData?.unitId
            name = foodData?.name
            barcode = foodData?.barcode
            energy = foodData?.energy
            carbs = foodData?.carbs
            fat = foodData?.fat
            protein = foodData?.protein
        }

        private var id: Long? = null
        private var unitId: Long? = null
        private var name: String? = null
        private var barcode: String? = null
        private var energy: FloatFieldState? = null
        private var carbs: FloatFieldState? = null
        private var fat: FloatFieldState? = null
        private var protein: FloatFieldState? = null

        fun id(id: Long?) = apply { this.id = id }
        fun unitId(unitId: Long?) = apply { this.unitId = unitId }
        fun name(name: String?) = apply { this.name = name }
        fun barcode(barcode: String?) = apply { this.barcode = barcode }
        fun energy(energy: FloatFieldState?) = apply { this.energy = energy }
        fun carbs(carbs: FloatFieldState?) = apply { this.carbs = carbs }
        fun fat(fat: FloatFieldState?) = apply { this.fat = fat }
        fun protein(protein: FloatFieldState?) = apply { this.protein = protein }

        fun build(): FoodData =
            FoodData(id, unitId, name, barcode, energy, carbs, fat, protein)
    }
}

@Parcelize
data class FloatFieldState(
    val value: String = "",
    val isError: Boolean = isFloatFieldError(value),
) : Parcelable {

    constructor(value: Float?) : this(value = value?.toString() ?: "")

    companion object {
        private fun isFloatFieldError(value: String): Boolean {
            if (value.trim().isEmpty()) {
                return false
            }
            return try {
                value.toFloat() <= 0
            } catch (ex: NumberFormatException) {
                true
            }
        }
    }
}