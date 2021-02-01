package com.rluisb.bankaccount.repository

import com.rluisb.bankaccount.domain.entity.AccountEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AccountRepository : MongoRepository<AccountEntity, String> {

    fun findByDocument(document: String) : Optional<AccountEntity>
    fun existsByDocument(document: String): Boolean
}