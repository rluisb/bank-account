package com.rluisb.bankaccount.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "deposits")
data class DepositTransactionEntity(
    @Id
    val id: String? = null,
    @Indexed
    val accountNumber: String,
    val amount: Long,
    val time: String
)
