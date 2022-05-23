package net.oleg.fd.room

import androidx.room.ColumnInfo

data class FoodDiarySum(
    @ColumnInfo(name = "energy", typeAffinity = ColumnInfo.REAL)
    val energy: Float,

    @ColumnInfo(name = "carbs", typeAffinity = ColumnInfo.REAL)
    val carbs: Float?,

    @ColumnInfo(name = "fat", typeAffinity = ColumnInfo.REAL)
    val fat: Float?,

    @ColumnInfo(name = "protein", typeAffinity = ColumnInfo.REAL)
    val protein: Float?,
)