package com.example.transactionsapp.data.datasource.local.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertReceipt(receipt: Receipt)

    @Query("SELECT * FROM receipts")
    suspend fun getAllReceipts(): LiveData<List<Receipt>>
}