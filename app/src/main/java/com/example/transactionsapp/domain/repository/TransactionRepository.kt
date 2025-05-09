package com.example.transactionsapp.domain.repository

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt

interface TransactionRepository {

    suspend fun insertReceipt(receipt: Receipt):Result<Unit>

    fun getAllReceipts(): Result<List<Receipt>>
}