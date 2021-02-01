package com.rluisb.bankaccount.api.account.dto.request

import org.jetbrains.annotations.NotNull


data class AccountRequest(
    @NotNull val name: String,
    @NotNull val document: String
)