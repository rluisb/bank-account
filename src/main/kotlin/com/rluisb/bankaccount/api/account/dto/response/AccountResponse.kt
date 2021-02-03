package com.rluisb.bankaccount.api.account.dto.response

data class AccountResponse(
    val accountNumber: String?,
    val name: String,
    val document: String,
    val balance: Long
)