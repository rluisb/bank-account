package com.rluisb.bankaccount.api.account

import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.domain.Account
import com.rluisb.bankaccount.service.AccountService
import io.swagger.annotations.Api
import org.slf4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(name = "account", consumes = ["application/json"], produces = ["application/json"])
@Api("Account API")
@Validated
class AccountApi(private val accountService: AccountService, private val logger: Logger) {

    @PostMapping
    fun createAccount(@Valid @RequestBody accountRequest: AccountRequest): ResponseEntity<AccountResponse> {
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