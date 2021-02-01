package com.rluisb.bankaccount.domain

import br.com.colman.simplecpfvalidator.isCpf
import java.util.*


class Account(
    val accountNumber: String? = null,
    val name: String,
    val document: String,
    val balance: Long = 0
) {

    companion object {
        const val LIMIT_FOR_DEPOSIT_OPERATION: Long = 2000L;
    }

    fun isDocumentValid(): Boolean {
        return this.document.isCpf(charactersToIgnore = listOf('.', '/', '-', '_'))
    }

    fun isValidValueForDepositOperation(): Boolean {
        return this.balance.compareTo(LIMIT_FOR_DEPOSIT_OPERATION) <= 0
    }
}