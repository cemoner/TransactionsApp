package com.example.transactionsapp.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "sale_items", foreignKeys = [
    ForeignKey(entity = Receipt::class, parentColumns = ["receiptNumber"], childColumns = ["receiptNumber"], onDelete = ForeignKey.CASCADE)
])
data class SaleItem(
    val receiptNumber:Int,
    val id:String,
    val label:String,
    val quantity:Int,
    val amount:Double,
)
