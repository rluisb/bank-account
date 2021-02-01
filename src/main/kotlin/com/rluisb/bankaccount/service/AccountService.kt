package com.rluisb.bankaccount.service

import com.rluisb.bankaccount.domain.Account
import com.rluisb.bankaccount.domain.entity.AccountEntity
import com.rluisb.bankaccount.exception.custom.DocumentAlreadyExists
import com.rluisb.bankaccount.exception.custom.InvalidDocument
import com.rluisb.bankaccount.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountService (private val accountRepository: AccountRepository){

    fun createAccount(account: Account): Account {

        if (!account.isDocumentValid()) {
            throw InvalidDocument("Document ${account.document} invalid. It must be a valid CPF. Ex.: 999.999.999-99")
        }

        if (accountRepository.existsByDocument(document = account.document)) {
            throw DocumentAlreadyExists("Document ${account.document} already exists.")
        }

        val accountEntity = AccountEntity(id = account.id, name = account.name, document = account.document, balance = account.balance)
        val createdAccount = this.accountRepository.save(accountEntity)
        return Account(id= createdAccount.id, name = createdAccount.name, document = createdAccount.document, balance = createdAccount.balance)
    }

    fun findAll(): List<Account> {
        return this.accountRepository.findAll().map { accountEntity -> Account(id= accountEntity.id, name = accountEntity.name, document = accountEntity.document, balance = accountEntity.balance) }
    }

}