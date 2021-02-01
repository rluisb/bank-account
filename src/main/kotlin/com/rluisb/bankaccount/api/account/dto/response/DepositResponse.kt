package com.rluisb.bankaccount.api.account.dto.response

data class DepositResponse(
    val accountNumber: String,
    val amount: Long,
    val time: String
)