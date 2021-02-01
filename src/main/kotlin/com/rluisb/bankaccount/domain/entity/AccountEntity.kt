package com.rluisb.bankaccount.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "accounts")
data class AccountEntity(
    @Id
    val id: String?,
    val name: String,
    @Indexed
    val document: String,
    val balance: Long
)
