package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteReceiptByReceiptNumber @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(receiptNumber: Int): Result<Unit> {
        return transactionRepository.deleteReceiptByReceiptNumber(receiptNumber)
    }
}