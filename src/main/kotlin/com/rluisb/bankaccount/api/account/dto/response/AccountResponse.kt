package com.rluisb.bankaccount.api.account.dto.response

data class AccountResponse(val id: String?,
                           val name: String,
                           val document: String,
                           val balance: Long)