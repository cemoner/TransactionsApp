package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredReceiptsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        filter: String
    ): Flow<List<Receipt>> {
        return transactionRepository.getFilteredReceipts(filter)
    }
}