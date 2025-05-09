package com.example.transactionsapp.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val receiptNumber: Int = 0,
    val receiptDateTime: String,
    val paymentType: String,
    val totalAmount: Double,
    val totalVat: Double,
)
