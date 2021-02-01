package com.rluisb.bankaccount.api.account.dto.request

data class TransferRequest(
    val destinationAccountNumber: String,
    val amount: Long
)