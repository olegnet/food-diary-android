package net.oleg.fd.room

fun FoodItem.replaceId(newId: Long) =
    FoodItem(newId, name, barcode, date, energy, carbs, fat, protein, itemIsDeleted)

fun FoodDiaryItem.replaceId(newId: Long) =
    FoodDiaryItem(newId, date, itemId, weight)
