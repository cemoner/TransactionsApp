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
            val totalVat = calculateVat(saleItems)
            val nextReceiptNumber = transactionDao.getReceiptCount() + 1

            if (nextReceiptNumber % 10 == 0) {

                val placeholderReceipt = Receipt(
                    receiptDateTime = receiptTime,
                    totalAmount = -1.0,
                    paymentType = "PLACEHOLDER",
                    totalVat = 0.0
                )
                transactionDao.insertReceipt(placeholderReceipt)

                Log.e("TransactionRepo", "Receipt number $nextReceiptNumber is a multiple of 10, transaction rejected")
                return Result.failure(Exception("Receipt number is a multiple of 10"))
            }
            val receipt = Receipt(
                receiptDateTime = receiptTime,
                totalAmount = totalBasketAmount,
                paymentType = paymentType,
                totalVat = totalVat
            )

            Log.d("TransactionRepo", "Receipt: $receipt")
            val receiptNumber = transactionDao.insertReceipt(receipt)
            if (receiptNumber > 0) {
                val saleItemList = saleItems.map { saleItem ->
                    SaleItem(
                        receiptNumber = receiptNumber.toInt(),
                        productId = saleItem.id,
                        label = saleItem.label,
                        quantity = saleItem.quantity,
                        amount = saleItem.amount,
                    )
                }
                for (saleItem in saleItemList) {
                    transactionDao.insertSaleItem(saleItem)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to insert receipt"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    override fun getSaleItemsByReceiptNumber(receiptNumber: Int): Flow<List<SaleItem>> {
        return transactionDao.getSaleItemsByReceiptNumber(receiptNumber)
    }

    override fun getAllSaleItems(): Flow<List<SaleItem>> {
        return transactionDao.getAllSaleItems()
    }
}