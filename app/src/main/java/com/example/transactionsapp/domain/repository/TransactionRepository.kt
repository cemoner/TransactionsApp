package com.example.transactionsapp.domain.repository

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import com.example.transactionsapp.data.model.ReceiverSaleItem

interface TransactionRepository {

    suspend fun insertTransactionDetails(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ):Result<Unit>

    suspend fun insertReceipt(receipt: Receipt): Result<Unit>

    suspend fun insertSaleItem(saleItem: SaleItem): Result<Unit>

    fun getAllReceipts(): Result<List<Receipt>>
}