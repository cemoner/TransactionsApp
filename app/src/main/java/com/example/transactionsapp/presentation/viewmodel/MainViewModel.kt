package com.example.transactionsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.domain.usecase.InsertTransactionDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertTransactionDetailsUseCase: InsertTransactionDetailsUseCase,
):ViewModel() {

    suspend fun insertTransactionDetails(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ):Result<Unit> {
        return try {
            Log.d("MainViewModel", "Inserting transaction details")
            insertTransactionDetailsUseCase(
                totalBasketAmount = totalBasketAmount,
                saleItems = saleItems,
                paymentType = paymentType
            )
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error inserting transaction details: ${e.message}")
            Result.failure(e)
        }
    }
}