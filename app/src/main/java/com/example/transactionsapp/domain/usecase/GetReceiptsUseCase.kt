package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.domain.repository.TransactionRepository
import javax.inject.Inject

class GetReceiptsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(): Result<List<Receipt>> {
        return transactionRepository.getAllReceipts()
    }
}