package com.example.transactionsapp.data.model

data class ReceiverSaleItem(
    val id: Int,
    val label: String,
    val quantity: Int,
    val totalPrice: Double,
    val vatRate: Int
)