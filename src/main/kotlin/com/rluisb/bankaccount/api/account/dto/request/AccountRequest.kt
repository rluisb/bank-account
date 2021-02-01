package com.rluisb.bankaccount.api.account.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AccountRequest(
    @field:NotBlank(message = "Field name must be filled.")
    val name: String,

    @field:Size(min = 11, max = 14, message = "Field document must have length minimum 11 characters and maximum 14 characters.")
    val document: String
)