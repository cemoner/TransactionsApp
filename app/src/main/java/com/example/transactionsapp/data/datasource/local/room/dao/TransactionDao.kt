package com.example.transactionsapp.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT COUNT(*) FROM receipts")
    suspend fun getReceiptCount(): Int

    @Insert
    suspend fun insertReceipt(receipt: Receipt): Long

    @Insert
    suspend fun insertSaleItem(saleItem: SaleItem): Long

    @Query("SELECT COUNT(*) FROM receipts WHERE receiptDateTime BETWEEN :startTime AND :endTime")
    suspend fun countReceiptsInTimeRange(startTime: String, endTime: String): Int

    @Query("SELECT * FROM receipts WHERE substr(receiptDateTime, 12, 2) = :hour AND totalAmount >= 0 AND paymentType != 'PLACEHOLDER' LIMIT 100")
    fun getReceiptsByHour(hour: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE substr(receiptDateTime, 12, 2) = :hour AND substr(receiptDateTime, 15, 1) = :tenthMinute AND totalAmount >= 0 AND paymentType != 'PLACEHOLDER' LIMIT 10")
    fun getReceiptsByTenthOfHour(hour: String, tenthMinute: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE substr(receiptDateTime, 12, 5) = :hourMinute AND totalAmount >= 0 AND paymentType != 'PLACEHOLDER' LIMIT 10")
    fun getReceiptsByMinute(hourMinute: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE receiptNumber LIKE :pattern || '%' AND totalAmount >= 0 AND paymentType != 'PLACEHOLDER' LIMIT 10")
    fun getReceiptsByNumberPattern(pattern: String): Flow<List<Receipt>>

    @Query("SELECT * FROM sale_items WHERE receiptNumber = :receiptNumber")
    fun getSaleItemsByReceiptNumber(receiptNumber: Int): Flow<List<SaleItem>>
}