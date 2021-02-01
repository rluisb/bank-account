package com.rluisb.bankaccount.repository

import com.rluisb.bankaccount.domain.entity.TransferTransactionEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface TransferTransactionRepository : MongoRepository<TransferTransactionEntity, String> {
}