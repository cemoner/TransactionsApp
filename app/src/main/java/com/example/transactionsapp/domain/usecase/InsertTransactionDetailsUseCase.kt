package com.example.transactionsapp.domain.usecase

import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.domain.repository.TransactionRepository
import javax.inject.Inject

class InsertTransactionDetailsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ):Result<Unit> = transactionRepository
        .insertTransactionDetails(
            totalBasketAmount = totalBasketAmount,
            saleItems = saleItems,
            paymentType = paymentType
        )
}