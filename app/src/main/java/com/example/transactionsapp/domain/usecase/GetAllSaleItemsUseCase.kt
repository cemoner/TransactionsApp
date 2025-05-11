package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.data.datasource.local.room.entity.SaleItem
import com.example.transactionsapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSaleItemsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<SaleItem>> {
        return transactionRepository.getAllSaleItems()
    }
}