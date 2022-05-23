package net.oleg.fd.room

import androidx.room.Embedded

data class FoodDiaryView(
    @Embedded
    val foodDiaryItem: FoodDiaryItem,

    @Embedded
    val foodItem: FoodItem,
)