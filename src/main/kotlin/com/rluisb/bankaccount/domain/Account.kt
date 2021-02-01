package com.rluisb.bankaccount.domain

import br.com.colman.simplecpfvalidator.isCpf


class Account(
    val accountNumber: String? = null,
    val name: String,
    val document: String,
    var balance: Long = 0
) {

    fun isDocumentValid(): Boolean {
        return this.document.isCpf(charactersToIgnore = listOf('.', '/', '-', '_'))
    }
}