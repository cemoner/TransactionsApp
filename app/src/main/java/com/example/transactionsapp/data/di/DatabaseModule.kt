package com.example.transactionsapp.data.di

import android.content.Context
import com.example.transactionsapp.data.datasource.local.room.database.TransactionsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTransactionsDatabase(@ApplicationContext context: Context): TransactionsDatabase {
        return TransactionsDatabase.getInstance(context)
    }
}