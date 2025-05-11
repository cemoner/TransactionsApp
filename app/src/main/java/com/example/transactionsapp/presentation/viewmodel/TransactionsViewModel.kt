package com.example.transactionsapp.presentation.viewmodel

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.domain.usecase.DeleteReceiptByReceiptNumber
import com.example.transactionsapp.domain.usecase.GetFilteredReceiptsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(

    private val getFilteredReceiptsUseCase: GetFilteredReceiptsUseCase,
    private val deleteReceiptsByReceiptNumber: DeleteReceiptByReceiptNumber,
): ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val currentHour = java.time.LocalDateTime.now().hour.toString()
    @RequiresApi(Build.VERSION_CODES.O)
    val filter = "T$currentHour"

    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    @RequiresApi(Build.VERSION_CODES.O)
    val receipts = _receipts
        .onStart { loadData(filter) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList<Receipt>()
        )

    private val _filterText = MutableStateFlow("")
    val filterText = _filterText.asStateFlow()


    @RequiresApi(Build.VERSION_CODES.O)
    fun setFilterText(text: String) {
        _filterText.value = text.uppercase(Locale.ROOT)
        loadData(
            filter = text.uppercase(Locale.ROOT)
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData(filter:String) = viewModelScope.launch {
        _filterText.value = filter
        getFilteredReceiptsUseCase(
            filter = filter
        ).collect {
            _receipts.value = it
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteReceipt(receiptNumber: Int) = viewModelScope.launch {
        val result = deleteReceiptsByReceiptNumber(receiptNumber)
        result.onSuccess {
            loadData(filter)
        }
        result.onFailure {
            loadData(filter)
            // Handle error
        }
    }

    fun prepareIntent(totalAmount: Double):Intent {
        return Intent("ACTION_CONFIRM_OPERATION").apply {
            component = ComponentName(
                "com.example.mainapp",
                "com.example.mainapp.presentation.ui.MainActivity"
            )
            putExtra("TOTAL_AMOUNT",totalAmount)
        }
    }
}