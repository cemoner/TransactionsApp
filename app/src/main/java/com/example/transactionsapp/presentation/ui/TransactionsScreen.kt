package com.example.transactionsapp.presentation.ui

import android.os.Build
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val filterText = viewModel.filterText.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFFADD8E6))
            .fillMaxWidth(0.6f)
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
                .height(8.dp))
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .defaultMinSize(minHeight = 256.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rno",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Time", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                Text("Amount", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            for (receipt in receipts) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.White)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        receipt.receiptNumber.toString(),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        receipt.receiptDateTime.split(" ")[1],
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        receipt.totalAmount.toString(),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Button(
            onClick = {

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