package com.rluisb.bankaccount.repository

import com.rluisb.bankaccount.domain.entity.AccountEntity
import com.rluisb.bankaccount.domain.entity.DepositTransactionEntity
import com.rluisb.bankaccount.domain.entity.TransferTransactionEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface DepositTransactionRepository : MongoRepository<DepositTransactionEntity, String> {
    fun findAllByAccountNumberOrderByTime(accountNumber: String): List<DepositTransactionEntity>
}