package com.example.transactionsapp.data.di

import com.example.transactionsapp.data.datasource.local.room.dao.TransactionDao
import com.example.transactionsapp.data.datasource.local.room.database.TransactionsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TransactionsDatabaseModule{

    @Provides
    fun provideTransactionDao(
        transactionsDatabase: TransactionsDatabase
    ): TransactionDao {
        return transactionsDatabase.transactionDao()
    }
}