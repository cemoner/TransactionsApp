package com.example.transactionsapp.domain.repository

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import com.example.transactionsapp.data.model.ReceiverSaleItem
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    suspend fun insertTransactionDetails(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ):Result<Unit>

    suspend fun insertReceipt(receipt: Receipt): Result<Unit>

    suspend fun insertSaleItem(saleItem: SaleItem): Result<Unit>

    suspend fun getFilteredReceipts(filter: String): Flow<List<Receipt>>
    
    fun getSaleItemsByReceiptNumber(receiptNumber: Int): Flow<List<SaleItem>>
}