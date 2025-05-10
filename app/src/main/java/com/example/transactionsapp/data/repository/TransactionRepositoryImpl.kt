package com.example.transactionsapp.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
):TransactionRepository {

    override suspend fun insertTransactionDetails(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ): Result<Unit> {
        return try {
            val receiptTime = getCurrentDate()
            Log.d("TransactionRepo", "Receipt time: $receiptTime")
            val totalVat = calculateVat(saleItems)
            val receiptNumber = getUniqueReceiptNumber(System.currentTimeMillis())
            Log.d("TransactionRepo", "Total VAT: $totalVat")
            val receipt = Receipt(
                receiptNumber = receiptNumber,
                receiptDateTime = receiptTime,
                totalAmount = totalBasketAmount,
                paymentType = paymentType,
                totalVat = totalVat
            )
            Log.d("TransactionRepo", "Receipt: $receipt")
            val responseCode = transactionDao.insertReceipt(receipt)
            if (responseCode > 0) {
                val saleItemList = saleItems.map { saleItem ->
                    SaleItem(
                        receiptNumber = receiptNumber,
                        productId = saleItem.id,
                        label = saleItem.label,
                        quantity = saleItem.quantity,
                        amount = saleItem.amount,
                    )
                }
                for (saleItem in saleItemList) {
                    transactionDao.insertSaleItem(saleItem)
                }
                Log.d("TransactionRepo", "Inserted sale items: $saleItemList")
                Result.success(Unit)
            } else {
                Log.e("TransactionRepo", "Failed to insert receipt")
                Result.failure(Exception("Failed to insert receipt"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUniqueReceiptNumber(timestamp: Long): Int {
        var attempts = 0
        var receiptNumber: Int

        do {
            val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH", Locale.getDefault())
            val hourStart = dateFormat.format(calendar.time) + ":00:00"
            val hourEnd = dateFormat.format(calendar.time) + ":59:59"

            // Base number + any retry attempts
            receiptNumber =
                transactionDao.countReceiptsInTimeRange(hourStart, hourEnd) + 1 + attempts

            // Check if this number already exists
            val exists = transactionDao.receiptNumberExists(receiptNumber)

            if (exists) {
                attempts++
            }
        } while (exists && attempts < 10) // Limit retries to avoid infinite loop

        return receiptNumber
    }


    private fun matchFilterPattern(filter: String): Int {

        if (filter.isEmpty()) return 0

        return when {
            // Time filters
            filter.startsWith("T") -> {
                when (filter.length) {
                    3 -> {
                        // THH format - validate it has 2 digits for hour
                        if (filter.substring(1).matches(Regex("\\d{2}"))) 1
                        else 0
                    }
                    4 -> {
                        // THHm format - validate it has 2 digits for hour and 1 for tenth
                        if (filter.substring(1).matches(Regex("\\d{3}"))) 2
                        else 0
                    }
                    5 -> {
                        // THHmm format - validate it has 2 digits for hour and 2 for minute
                        if (filter.substring(1).matches(Regex("\\d{4}"))) 3
                        else 0
                    }
                    else -> 0
                }
            }
            // Receipt number filters
            filter.startsWith("R") -> {
                // Validate it has at least one digit after R
                if (filter.length > 1 && filter.substring(1).matches(Regex("\\d+"))) 4
                else 0
            }
            else -> 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFilteredReceipts(filter: String): Flow<List<Receipt>> {
        // current hour
        val currentHour = java.time.LocalDateTime.now().hour.toString()
        return when (val filterType = matchFilterPattern(filter)) {
            0 -> {
                // Invalid filter
                Log.e("TransactionRepo", "Invalid filter: $filter")
                transactionDao.getReceiptsByHour(currentHour)
            }
            1 -> {
                // Hour filter (THH)
                Log.d("TransactionRepo", "Hour filter: $filter")
                val hour = filter.substring(1, 3)
                Log.d("TransactionRepo", "Hour: $hour")
                transactionDao.getReceiptsByHour(hour)
            }
            2 -> {
                // Tenth of hour filter (THHm)
                Log.d("TransactionRepo", "Tenth of hour filter: $filter")
                val hour = filter.substring(1, 3)
                val tenthMinute = filter.substring(3, 4)
                transactionDao.getReceiptsByTenthOfHour(hour, tenthMinute)
            }
            3 -> {
                // Minute filter (THHmm)
                Log.d("TransactionRepo", "Minute filter: $filter")
                val hour = filter.substring(1, 3)
                val minute = filter.substring(3, 5)
                val hourMinute = "$hour:$minute"
                transactionDao.getReceiptsByMinute(hourMinute)
            }
            4 -> {
                // Receipt number filter (Rn)
                val pattern = filter.substring(1)
                transactionDao.getReceiptsByNumberPattern(pattern)
            }
            else -> transactionDao.getReceiptsByHour("T$filter")
        }.catch { exception ->
            Log.e("TransactionRepo", "Error filtering receipts", exception)
            emit(emptyList())
        }.onEach { receipts ->
            Log.d("TransactionRepo", "Filtered receipts count: ${receipts.size}")
            receipts.forEach { receipt ->
                Log.d("TransactionRepo", "Receipt found: $receipt")
            }
        }
    }

    private fun calculateVat(saleItems: List<ReceiverSaleItem>): Double {
        var totalVat = 0.0

        for (item in saleItems) {

            val amountWithoutVat = item.amount / (1 + (item.vatRate / 100.0))

            val itemVatAmount = item.amount - amountWithoutVat


            totalVat += itemVatAmount
        }

        return totalVat
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    override suspend fun insertReceipt(receipt: Receipt): Result<Unit> {
        return try {
            val insertedId = transactionDao.insertReceipt(receipt)
            if (insertedId > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to insert receipt"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertSaleItem(saleItem: SaleItem): Result<Unit> {
        return try {
            val insertedId = transactionDao.insertSaleItem(saleItem)
            if (insertedId > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to insert sale item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSaleItemsByReceiptNumber(receiptNumber: Int): Flow<List<SaleItem>> {
        return transactionDao.getSaleItemsByReceiptNumber(receiptNumber)
    }
}