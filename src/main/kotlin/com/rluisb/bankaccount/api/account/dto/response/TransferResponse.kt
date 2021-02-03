package com.rluisb.bankaccount.api.account.dto.response

data class TransferResponse(
    val targetAccountNumber: String,
    val amount: Long,
    val time: String
)