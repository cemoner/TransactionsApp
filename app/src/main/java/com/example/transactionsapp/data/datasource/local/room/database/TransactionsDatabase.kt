package com.example.transactionsapp.data.datasource.local.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem

@Database(
    entities = [
        Receipt::class,
        SaleItem::class
    ],
    version = 4,
    exportSchema = false
)
abstract class TransactionsDatabase : RoomDatabase() {


    abstract fun transactionDao(): TransactionDao

    fun resetDatabase(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
        INSTANCE = null
    }

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the correct schema
                database.execSQL("CREATE TABLE sale_items_new (" +
                        "receiptNumber INTEGER NOT NULL, " +
                        "id INTEGER NOT NULL, " +
                        "label TEXT NOT NULL, " +
                        "quantity INTEGER NOT NULL, " +
                        "amount REAL NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "FOREIGN KEY(receiptNumber) REFERENCES receipts(receiptNumber) ON DELETE CASCADE)")

                // Copy data from the old table to the new one
                database.execSQL("INSERT INTO sale_items_new SELECT * FROM sale_items")

                // Remove the old table
                database.execSQL("DROP TABLE sale_items")

                // Rename the new table to the original name
                database.execSQL("ALTER TABLE sale_items_new RENAME TO sale_items")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Modify the receipt table to change primary key from auto-generation to not at all
                // This might require recreating the table with the new schema
                database.execSQL("CREATE TABLE receipts_new (" +
                        "receiptNumber INTEGER PRIMARY KEY NOT NULL, " +
                        "receiptDateTime TEXT NOT NULL, " +
                        "totalAmount REAL NOT NULL, " +
                        "paymentType TEXT NOT NULL, " +
                        "totalVat REAL NOT NULL)")

                database.execSQL("INSERT INTO receipts_new SELECT * FROM receipts")
                database.execSQL("DROP TABLE receipts")
                database.execSQL("ALTER TABLE receipts_new RENAME TO receipts")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new table with structure matching your current entity
                database.execSQL(
                    "CREATE TABLE sale_items_new (" +
                            "saleItemId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "receiptNumber INTEGER NOT NULL, " +
                            "productId INTEGER NOT NULL, " +
                            "label TEXT NOT NULL, " +
                            "quantity INTEGER NOT NULL, " +
                            "amount REAL NOT NULL, " +
                            "FOREIGN KEY(receiptNumber) REFERENCES receipts(receiptNumber) ON DELETE CASCADE)"
                )

                // Copy data from old table to new table
                database.execSQL(
                    "INSERT INTO sale_items_new (receiptNumber, productId, label, quantity, amount) " +
                            "SELECT receiptNumber, id, label, quantity, amount " +
                            "FROM sale_items"
                )

                // Drop old table
                database.execSQL("DROP TABLE sale_items")

                // Rename new table to original name
                database.execSQL("ALTER TABLE sale_items_new RENAME TO sale_items")
            }
        }
        const val DATABASE_NAME = "transactions_database"
        @Volatile
        private var INSTANCE: TransactionsDatabase? = null
        fun getInstance(context: Context): TransactionsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionsDatabase::class.java,
                    DATABASE_NAME
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }

    }
}