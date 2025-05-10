package com.example.transactionsapp.data.model

data class ReceiverSaleItem(
    val id: Int,
    val label: String,
    val quantity: Int,
    val amount: Double,
    val vatRate: Int,
)