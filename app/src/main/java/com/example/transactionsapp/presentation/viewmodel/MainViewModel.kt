package com.example.transactionsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.domain.usecase.InsertTransactionDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertTransactionDetailsUseCase: InsertTransactionDetailsUseCase,
):ViewModel() {

    fun insertTransactionDetails(
        totalBasketAmount: Double,
        saleItems: List<ReceiverSaleItem>,
        paymentType: String
    ) = viewModelScope.launch {
        insertTransactionDetailsUseCase(
            totalBasketAmount = totalBasketAmount,
            saleItems = saleItems,
            paymentType = paymentType
        )
    }

}