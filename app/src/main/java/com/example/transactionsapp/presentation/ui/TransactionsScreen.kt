package com.example.transactionsapp.presentation.ui

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.transactionsapp.data.datasource.local.room.entity.Receipt
import com.example.transactionsapp.presentation.viewmodel.TransactionsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionsScreen(
) {
    val viewModel:TransactionsViewModel = hiltViewModel()
    TransactionsContent(
        viewModel
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionsContent(
    viewModel: TransactionsViewModel
) {
    val context = LocalContext.current
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val filterText = viewModel.filterText.collectAsStateWithLifecycle()
    val selectedReceipt = remember { mutableStateOf<Receipt?>(null) }

    val rNoList = receipts.map { it.receiptNumber }
    val timeList = receipts.map { it.receiptDateTime.split(" ")[1] }
    val amountList = receipts.map { it.totalAmount }


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultMessage = result.data?.getIntExtra("RESPONSE_CODE", -1)
        if(resultMessage == 0) {
            val receipt = selectedReceipt.value
            if (receipt != null) {
                viewModel.deleteReceipt(receipt.receiptNumber)
                Log.d("TransactionsScreen", "Receipt ${receipt.receiptNumber} deleted successfully.")
            } else {
                Log.e("TransactionsScreen", "Error: Receipt is null.")
            }
        }
        else if(resultMessage == -1){
            Log.d("TransactionsScreen", "Receipt deletion cancelled.")

        }
        else {
            Log.e("TransactionsScreen", "Error: $resultMessage")
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFFADD8E6))
            .fillMaxWidth(0.7f)
            .border(1.dp, Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .height(16.dp))
        TextField(
            value = filterText.value,
            onValueChange = { viewModel.setFilterText(it) },
            modifier = Modifier
                .padding(8.dp)
                .background(Color.White),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
            ),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )
        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        Row(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .fillMaxWidth()
                .defaultMinSize(minHeight = 200.dp)
                    ,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp,top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "RNo",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                for(rno in rNoList){
                    Text(
                        text = rno.toString(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Time",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                for(time in timeList){
                    Text(
                        text = time,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
            Column(
                modifier = Modifier.padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Amount",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                for(amount in amountList){
                    Text(
                        text = amount.toString(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(
                modifier = Modifier.padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                for(receipt in receipts){
                    Button(
                        onClick = {
                            selectedReceipt.value = receipt
                            val intent = viewModel.prepareIntent(receipt.totalAmount)
                            launcher.launch(intent)
                        },
                        modifier = Modifier
                            .height(16.5.dp)
                            .padding(3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF749E43),
                            contentColor = Color.Black
                        ),
                        shape = RectangleShape,
                        contentPadding = PaddingValues(
                            start = 0.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        ),
                    ) {
                        Text("Cancel",fontSize = 9.sp)
                    }
                }
            }
        }


        Button(
            onClick = {
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .padding(8.dp)
                .height(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF749E43),
                contentColor = Color.Black
            ),
            shape = RectangleShape,
            contentPadding = PaddingValues(
                start = 48.dp,
                top = 0.dp,
                end = 48.dp,
                bottom = 0.dp
            ),
        ) {
            Text("Return",fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}