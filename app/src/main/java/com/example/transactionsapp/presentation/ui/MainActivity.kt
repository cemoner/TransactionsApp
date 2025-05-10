package com.example.transactionsapp.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.presentation.viewmodel.MainViewModel
import com.example.transactionsapp.ui.theme.TransactionsAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == "ACTION_NEW_TRANSACTION") {
            val saleItemsJson = intent.getStringExtra("SALE_ITEMS_JSON") ?: ""
            val totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
            val paymentType = intent.getStringExtra("PAYMENT_TYPE") ?: ""


            val gson = Gson()
            val type = object : TypeToken<List<ReceiverSaleItem>>() {}.type
            val saleItems: List<ReceiverSaleItem> = gson.fromJson(saleItemsJson, type)
            viewModel.insertTransactionDetails(
                totalBasketAmount = totalAmount,
                saleItems = saleItems,
                paymentType = paymentType
            )
        }

        setContent {
            TransactionsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Transaction App",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TransactionsAppTheme {
        Greeting("Android")
    }
}