package com.example.transactionsapp.data.repository

import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.domain.repository.TransactionRepository
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
            val receipt = Receipt(
                receiptDateTime = receiptTime,
                totalAmount = totalBasketAmount,
                paymentType = paymentType,
                totalVat = totalVat
            )
            val receiptNumber = transactionDao.insertReceipt(receipt)
            if (receiptNumber > 0) {
                val saleItemList = saleItems.map { saleItem ->
                    SaleItem(
                        receiptNumber = receiptNumber,
                        id = saleItem.id,
                        label = saleItem.label,
                        quantity = saleItem.quantity,
                        amount = saleItem.totalPrice,
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

    override fun getAllReceipts(): Result<List<Receipt>> {
        TODO("Not yet implemented")
    }

    private fun calculateVat(saleItems: List<ReceiverSaleItem>): Double {
        var totalVat = 0.0

        for (item in saleItems) {

            val amountWithoutVat = item.totalPrice / (1 + (item.vatRate / 100.0))

            val itemVatAmount = item.totalPrice - amountWithoutVat


            totalVat += itemVatAmount
        }

        return totalVat
    }
    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
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
}