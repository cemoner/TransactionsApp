package com.example.transactionsapp.presentation.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transactionsapp.presentation.viewmodel.TransactionsViewModel

@Composable
fun TransactionsScreen(
) {
    val viewModel:TransactionsViewModel = hiltViewModel()
    TransactionsContent(
        viewModel
    )
}
@Composable
fun TransactionsContent(
    viewModel: TransactionsViewModel
) {

}