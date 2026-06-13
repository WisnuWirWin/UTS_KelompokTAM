package com.le.uts_tam.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.le.uts_tam.data.local.dao.CustomerDao
import com.le.uts_tam.data.local.dao.ItemDao
import com.le.uts_tam.data.local.dao.OwnerDao
import com.le.uts_tam.data.local.dao.VehicleDao
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.model.dataclass.Vehicles

@Database(entities = [Customers::class, Vehicles::class, Items::class, Owners::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun itemDao(): ItemDao
    abstract fun ownerDao(): OwnerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bengkel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
