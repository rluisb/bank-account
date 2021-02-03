package com.rluisb.bankaccount.exception.custom

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class ErrorsDetails(
    val time: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now()),
    val message: String,
    val details: Any?
)
