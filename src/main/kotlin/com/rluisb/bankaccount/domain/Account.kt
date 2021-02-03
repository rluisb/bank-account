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

    override fun toString(): String {
        return "Account(accountNumber=$accountNumber, name='$name', document='$document', balance=$balance)"
    }


}