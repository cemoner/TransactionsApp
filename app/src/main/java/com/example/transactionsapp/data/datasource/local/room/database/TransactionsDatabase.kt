package com.example.transactionsapp.data.datasource.local.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem

@Database(
    entities = [
        Receipt::class,
        SaleItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TransactionsDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "transactions_database"
        @Volatile
        private var INSTANCE: TransactionsDatabase? = null
        fun getInstance(context: Context): TransactionsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionsDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}