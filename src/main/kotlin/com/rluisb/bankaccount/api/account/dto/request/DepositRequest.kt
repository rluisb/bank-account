package com.rluisb.bankaccount.api.account.dto.request

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class DepositRequest(
    @field:Min(value = 0, message = "Amount cannot be negative.")
    val amount: Long
)