package com.rluisb.bankaccount.api.account

import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.request.DepositRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.domain.Account
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
class AccountApi(private val accountService: AccountService, private val logger: Logger) {

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

    @PatchMapping("{accountNumber}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deposit(
        @PathVariable("accountNumber") accountNumber: String,
        @Valid @RequestBody depositRequest: DepositRequest
    ): ResponseEntity<AccountResponse> {
        this.logger.info("Starting deposit with values: {} for account: {}", depositRequest.toString(), accountNumber)

        this.logger.info("Searching account for accountNumber: {}", accountNumber)
        val account = this.accountService.findByAccountNumber(accountNumber)!!

        this.logger.info("Account found: {}", account.toString())
        this.logger.info("Updating balance with value: {}", depositRequest.amount)

        this.logger.info("Executing account deposit.")
        val accountWithBalanceForUpdate = Account(
            accountNumber = account.accountNumber,
            name = account.name,
            document = account.document,
            balance = depositRequest.amount
        )
        val updatedAccount = this.accountService.deposit(accountWithBalanceForUpdate)
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

    @GetMapping
    fun getAllAccounts(): ResponseEntity<List<AccountResponse>> {
        this.logger.info("Fetching all existing accounts.")
        val accountList = this.accountService.findAll().map { account ->
            AccountResponse(
                accountNumber = account.accountNumber,
                name = account.name,
                document = account.document,
                balance = account.balance
            )
        }

        this.logger.info("Accounts found: {}", accountList)

        return ResponseEntity.ok(accountList)
    }
}