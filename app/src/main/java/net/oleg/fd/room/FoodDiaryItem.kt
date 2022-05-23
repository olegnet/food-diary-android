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