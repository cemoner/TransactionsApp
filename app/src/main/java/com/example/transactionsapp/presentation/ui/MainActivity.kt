package com.example.transactionsapp.presentation.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transactionsapp.data.model.ReceiverSaleItem
import com.example.transactionsapp.presentation.viewmodel.MainViewModel
import com.example.transactionsapp.ui.theme.TransactionsAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)


        setContent {
            TransactionsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainApp(
                        innerPadding,
                    )
                }
            }
        }
    }
    private fun handleIncomingIntent(intent:Intent?) {
        when(intent?.action) {
            "ACTION_NEW_TRANSACTION" -> {
                val saleItemsJson = intent.getStringExtra("SALE_ITEMS_JSON") ?: ""
                val totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
                val paymentType = intent.getStringExtra("PAYMENT_TYPE") ?: ""

                val gson = Gson()
                val type = object : TypeToken<List<ReceiverSaleItem>>() {}.type
                val saleItems: List<ReceiverSaleItem> = gson.fromJson(saleItemsJson, type)
                lifecycleScope.launch {
                    var result:Result<Unit> = Result.failure(Exception("No result"))
                    async {
                         result = viewModel.insertTransactionDetails(
                            totalBasketAmount = totalAmount,
                            saleItems = saleItems,
                            paymentType = paymentType
                        )
                    }.await()

                    result.onSuccess {
                        val resultIntent = Intent().apply {
                            putExtra("RESULT", "0")
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    result.onFailure {
                        val resultIntent = Intent().apply {
                            putExtra("RESULT", "-1")
                        }
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                        finish()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp(
    innerPadding: PaddingValues,
) {
    val navHostController = rememberNavController()
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        NavHost(
            navController = navHostController,
            startDestination = "transactionsScreen",
        ){
            composable("transactionsScreen") {
                TransactionsScreen()
            }
        }
    }
}