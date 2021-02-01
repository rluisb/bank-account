package com.rluisb.bankaccount.service

import com.rluisb.bankaccount.api.account.dto.request.DepositRequest
import com.rluisb.bankaccount.domain.Account
import com.rluisb.bankaccount.domain.Deposit
import com.rluisb.bankaccount.domain.entity.AccountEntity
import com.rluisb.bankaccount.domain.entity.DepositTransactionEntity
import com.rluisb.bankaccount.exception.custom.AccountNotFoundException
import com.rluisb.bankaccount.exception.custom.DocumentAlreadyExistsException
import com.rluisb.bankaccount.exception.custom.InvalidDocumentException
import com.rluisb.bankaccount.exception.custom.InvalidValueForDepositException
import com.rluisb.bankaccount.repository.AccountRepository
import com.rluisb.bankaccount.repository.DepositTransactionRepository
import org.slf4j.Logger

import org.springframework.stereotype.Service

@Service
class AccountService(
    private val logger: Logger,
    private val accountRepository: AccountRepository,
    private val depositTransactionRepository: DepositTransactionRepository
) {

    companion object {
        const val MIN_LIMIT_FOR_DEPOSIT_OPERATION: Long = 0L
        const val MAX_LIMIT_FOR_DEPOSIT_OPERATION: Long = 2000L
    }

    fun create(account: Account): Account {
        this.logger.info("Starting account creation process validation for values: {}", account.toString())

        this.logger.info("Validating document: {}", account.document)
        if (!account.isDocumentValid()) {
            this.logger.error("Document: {} is invalid", account.document)
            throw InvalidDocumentException("Document ${account.document} invalid. It must be a valid CPF. Ex.: 999.999.999-99")
        }

        this.logger.info("Checking if document: {} already exists.", account.document)
        if (accountRepository.existsByDocument(document = account.document)) {
            this.logger.error("Document: {} already exists.", account.document)
            throw DocumentAlreadyExistsException("Document ${account.document} already exists.")
        }

        this.logger.info("Account is valid for creation")
        this.logger.info("Converting Account to AccountEntity.")
        val accountEntity = AccountEntity(
            accountNumber = account.accountNumber,
            name = account.name,
            document = account.document,
            balance = account.balance
        )

        this.logger.info("Converted AccountEntity: {}", accountEntity.toString())
        this.logger.info("Saving accountEntity into database.")
        val createdAccount = this.accountRepository.save(accountEntity)

        this.logger.info("Created account from database: {}", createdAccount.toString())
        this.logger.info("Converting AccountEntity to Account.")
        val createdAccountResponse = Account(
            accountNumber = createdAccount.accountNumber,
            name = createdAccount.name,
            document = createdAccount.document,
            balance = createdAccount.balance
        )

        this.logger.info("Converted Account: {}", createdAccountResponse.toString())

        return createdAccountResponse
    }

    fun findAll(): List<Account> {
        return this.accountRepository.findAll().map { accountEntity ->
            Account(
                accountNumber = accountEntity.accountNumber,
                name = accountEntity.name,
                document = accountEntity.document,
                balance = accountEntity.balance
            )
        }
    }

    fun findByAccountNumber(accountNumber: String): Account? {
        this.logger.info("Starting search for account by accountNumber: {}", accountNumber)
        return this.accountRepository.findById(accountNumber).map { accountEntity ->
            Account(
                accountNumber = accountEntity.accountNumber,
                name = accountEntity.name,
                document = accountEntity.document,
                balance = accountEntity.balance
            )
        }.orElseThrow {
            this.logger.error("Account not found for {}", accountNumber)
            AccountNotFoundException("Account not found for $accountNumber")
        }
    }

    fun deposit(accountNumber: String, depositRequest: DepositRequest): Account {
        this.logger.info("Starting balance update for account: {}", accountNumber)

        this.logger.info("Searching account for accountNumber: {}", accountNumber)
        val account = this.findByAccountNumber(accountNumber)!!

        this.logger.info("Account found: {}", account.toString())
        this.logger.info("Updating balance with value: {}", depositRequest.amount)

        this.logger.info("Validating amount for deposit.")
        when {
            depositRequest.amount < MIN_LIMIT_FOR_DEPOSIT_OPERATION -> {
                val message = "Value ${account.balance} cannot be negative."
                this.logger.error(message)
                throw InvalidValueForDepositException(message)
            }

            depositRequest.amount > MAX_LIMIT_FOR_DEPOSIT_OPERATION -> {
                val message =
                    "Value ${account.balance} cannot exceed the security limit of $MAX_LIMIT_FOR_DEPOSIT_OPERATION."
                this.logger.error(message)
                throw InvalidValueForDepositException(message)
            }
        }

        this.logger.info("Converting Account to AccountEntity.")
        val accountEntity = AccountEntity(
            accountNumber = account.accountNumber,
            name = account.name,
            document = account.document,
            balance = account.balance.plus(depositRequest.amount)
        )

        this.logger.info("Converted AccountEntity: {}", accountEntity.toString())
        this.logger.info("Updating account balance into database")

        val accountWithUpdatedBalance = this.accountRepository.save(accountEntity)

        this.logger.info("Updated account from database: {}", accountWithUpdatedBalance.toString())
        this.logger.info("Converting AccountEntity to Account.")

        val updatedAccount = Account(
            accountNumber = accountWithUpdatedBalance.accountNumber,
            name = accountWithUpdatedBalance.name,
            document = accountWithUpdatedBalance.document,
            balance = accountWithUpdatedBalance.balance
        )

        this.logger.info("Converted Account: {}", accountWithUpdatedBalance.toString())

        val deposit =
            Deposit(accountNumber = accountNumber, amount = depositRequest.amount)

        this.logger.info("Generating deposit transaction information: {}", deposit.toString())
        this.logger.info("Converting Deposit to DepositTransactionEntity.")

        val depositTransactionEntity = DepositTransactionEntity(
            accountNumber = deposit.accountNumber,
            amount = deposit.amount,
            time = deposit.time
        )

        this.logger.info("Converted DepositTransactionEntity: {}", depositTransactionEntity.toString())

        this.logger.info("Saving deposit transaction for account number: {}", updatedAccount.accountNumber)
        val savedDepositTransaction = this.depositTransactionRepository.insert(depositTransactionEntity)

        this.logger.info(
            "Saved deposit transaction: {} for account number: {}",
            savedDepositTransaction.toString(),
            updatedAccount.accountNumber
        )

        return updatedAccount
    }

    fun findAllDeposits(accountNumber: String): List<Deposit> {
        this.logger.info("Starting search for deposit by accountNumber: {}", accountNumber)
        return this.depositTransactionRepository.findAllByAccountNumberOrderByTime(accountNumber).map {
            Deposit(
                accountNumber = it.accountNumber,
                amount = it.amount,
                time = it.time
            )
        }
    }

}