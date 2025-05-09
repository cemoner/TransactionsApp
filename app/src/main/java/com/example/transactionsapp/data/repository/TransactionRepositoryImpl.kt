package com.example.transactionsapp.data.repository

import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.domain.repository.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
):TransactionRepository {

    override suspend fun insertReceipt(receipt: Receipt): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getAllReceipts(): Result<List<Receipt>> {
        TODO("Not yet implemented")
    }
}