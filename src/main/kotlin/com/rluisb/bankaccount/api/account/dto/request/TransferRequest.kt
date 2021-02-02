package com.rluisb.bankaccount.api.account.dto.request

data class TransferRequest(
    val targetAccountNumber: String,
    val amount: Long
)