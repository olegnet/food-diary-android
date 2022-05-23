package net.oleg.fd.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FoodItem::class, FoodDiaryItem::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

    companion object {

        private const val DATABASE = "food.db"

        @Volatile
        private var foodDatabase: FoodDatabase? = null

        fun getDatabase(
            context: Context
        ): FoodDatabase {
            return foodDatabase ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        FoodDatabase::class.java,
                        DATABASE
                    )
                    .build()
                foodDatabase = instance
                instance
            }
        }
    }
}