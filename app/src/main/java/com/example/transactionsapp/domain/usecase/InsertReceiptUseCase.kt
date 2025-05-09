package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.domain.repository.TransactionRepository
import javax.inject.Inject

class InsertReceiptUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
)
{
    suspend operator fun invoke(receipt: Receipt): Result<Unit> {
        return transactionRepository.insertReceipt(receipt)
    }
}