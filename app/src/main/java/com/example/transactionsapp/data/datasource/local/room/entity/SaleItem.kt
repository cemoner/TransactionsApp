package com.example.transactionsapp.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(entity = Receipt::class, parentColumns = ["receiptNumber"],
            childColumns = ["receiptNumber"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SaleItem(
    @PrimaryKey(autoGenerate = true)
    val saleItemId: Int = 0,
    val receiptNumber: Int,
    val productId: Int,
    val label: String,
    val quantity: Int,
    val amount: Double
)