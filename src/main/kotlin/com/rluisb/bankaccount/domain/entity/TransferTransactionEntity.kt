package com.rluisb.bankaccount.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transactions")
data class TransferTransactionEntity(
    @Id
    val id: String? = null,
    val originAccountNumber: String,
    @Indexed
    val targetAccountNumber: String,
    val amount: Long,
    val time: String
)
