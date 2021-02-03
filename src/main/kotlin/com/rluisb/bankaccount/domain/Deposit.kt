package com.rluisb.bankaccount.domain

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class Deposit(
    val accountNumber: String,
    val amount: Long,
    val time: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now()),
)