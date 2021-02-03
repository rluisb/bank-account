package com.rluisb.bankaccount.api.account

import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.request.DepositRequest
import com.rluisb.bankaccount.api.account.dto.request.TransferRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.api.account.dto.response.DepositResponse
import com.rluisb.bankaccount.api.account.dto.response.TransferResponse
import com.rluisb.bankaccount.domain.Account
import com.rluisb.bankaccount.domain.Transfer
import com.rluisb.bankaccount.service.AccountService
import io.swagger.annotations.Api
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(value = ["account"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Api("Account API")
@Validated
class AccountApi(
    private val logger: Logger,
    private val accountService: AccountService
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody accountRequest: AccountRequest): ResponseEntity<AccountResponse> {
        this.logger.info("Starting account creation with values: {}", accountRequest.toString())

        this.logger.info("Converting accountRequest to account domain.")
        val accountForCreation = Account(name = accountRequest.name, document = accountRequest.document)
        this.logger.info("Converted account domain: {}", accountForCreation.toString())

        this.logger.info("Executing account creation.")
        val createdAccount = this.accountService.create(accountForCreation)
        this.logger.info("Account created successfully: {}", createdAccount.toString())

        val accountResponse = AccountResponse(
            accountNumber = createdAccount.accountNumber,
            name = createdAccount.name,
            document = createdAccount.document,
            balance = createdAccount.balance
        )

        this.logger.info("Account response: {}", accountResponse.toString())

        return ResponseEntity.ok(accountResponse)
    }

    @PatchMapping("{accountNumber}/deposit", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deposit(
        @PathVariable("accountNumber") accountNumber: String,
        @Valid @RequestBody depositRequest: DepositRequest
    ): ResponseEntity<AccountResponse> {
        this.logger.info("Starting deposit with values: {} for account: {}", depositRequest.toString(), accountNumber)

        this.logger.info("Executing account deposit.")
        val updatedAccount = this.accountService.deposit(accountNumber = accountNumber, depositRequest = depositRequest)
        this.logger.info("Account balance updated successfully: {}", updatedAccount.toString())

        val accountResponse = AccountResponse(
            accountNumber = updatedAccount.accountNumber,
            name = updatedAccount.name,
            document = updatedAccount.document,
            balance = updatedAccount.balance
        )

        this.logger.info("Account response: {}", accountResponse.toString())

        return ResponseEntity.ok(accountResponse)
    }

    @PatchMapping("{originAccountNumber}/transfer", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun transfer(
        @PathVariable("originAccountNumber") originAccountNumber: String,
        @Valid @RequestBody transferRequest: TransferRequest
    ): ResponseEntity<AccountResponse> {
        this.logger.info(
            "Starting transfer with values: {} from account: {}",
            originAccountNumber,
            transferRequest.targetAccountNumber
        )

        this.logger.info("Converting transferRequest to Transfer domain.")
        val transferForUpdate = Transfer(
            originAccountNumber = originAccountNumber,
            targetAccountNumber = transferRequest.targetAccountNumber,
            amount = transferRequest.amount
        )
        this.logger.info("Converted transfer domain: {}", transferForUpdate.toString())

        this.logger.info("Executing account transfer.")
        val updatedAccount = this.accountService.transfer(transferForUpdate)
        this.logger.info("Transfer executed successfully: {}", updatedAccount.toString())

        val accountResponse = AccountResponse(
            accountNumber = updatedAccount.accountNumber,
            name = updatedAccount.name,
            document = updatedAccount.document,
            balance = updatedAccount.balance
        )

        this.logger.info("Account response: {}", accountResponse.toString())

        return ResponseEntity.ok(accountResponse)
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<List<AccountResponse>> {
        this.logger.info("Fetching all existing accounts.")
        val accountList = this.accountService.findAll().map {
            AccountResponse(
                accountNumber = it.accountNumber,
                name = it.name,
                document = it.document,
                balance = it.balance
            )
        }

        this.logger.info("Accounts found: {}", accountList)

        return ResponseEntity.ok(accountList)
    }

    @GetMapping("{accountNumber}")
    fun getAccountByAccountNumber(@PathVariable("accountNumber") accountNumber: String): ResponseEntity<AccountResponse> {
        this.logger.info("Fetching account.")
        val account = this.accountService.findByAccountNumber(accountNumber = accountNumber)!!

        val accountResponse = AccountResponse(
            accountNumber = account.accountNumber,
            name = account.name,
            document = account.document,
            balance = account.balance
        )

        this.logger.info("Accounts found: {}", accountResponse)

        return ResponseEntity.ok(accountResponse)
    }

    @GetMapping("{accountNumber}/deposit")
    fun getAllDepositsForAccount(@PathVariable("accountNumber") accountNumber: String): ResponseEntity<List<DepositResponse>> {
        this.logger.info("Fetching all existing deposits for account.")
        val accountDepositList = this.accountService.findAllDeposits(accountNumber).map {
            DepositResponse(
                amount = it.amount,
                time = it.time
            )
        }

        this.logger.info("Accounts deposits found: {}", accountDepositList)

        return ResponseEntity.ok(accountDepositList)
    }

    @GetMapping("{originAccountNumber}/transfer")
    fun getAllTransfersForAccount(@PathVariable("originAccountNumber") originAccountNumber: String): ResponseEntity<List<TransferResponse>> {
        this.logger.info("Fetching all existing transfers for account.")
        val accountTransferList = this.accountService.findAllTransfers(originAccountNumber = originAccountNumber).map {
            TransferResponse(
                targetAccountNumber = it.targetAccountNumber,
                amount = it.amount,
                time = it.time
            )
        }

        this.logger.info("Accounts transfers found: {}", accountTransferList)

        return ResponseEntity.ok(accountTransferList)
    }
}