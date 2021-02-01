package com.rluisb.bankaccount.api.account

import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.domain.Account
import com.rluisb.bankaccount.service.AccountService
import io.swagger.annotations.Api
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(name = "account", consumes= ["application/json"], produces= ["application/json"])
@Api("Account API")
@Validated
class AccountApi (val accountService: AccountService){

    @PostMapping
    fun createAccount(@Valid @RequestBody accountRequest: AccountRequest) : ResponseEntity<AccountResponse>{
        val accountForCreation = Account(name = accountRequest.name, document = accountRequest.document)
        val createdAccount = this.accountService.createAccount(accountForCreation)
        return ResponseEntity.ok(AccountResponse(accountNumber= createdAccount.accountNumber, name = createdAccount.name, document = createdAccount.document, balance = createdAccount.balance))
    }

    @GetMapping
    fun getAllAccounts() : ResponseEntity<List<AccountResponse>>{
        val accountList = this.accountService.findAll().map { account -> AccountResponse(accountNumber= account.accountNumber, name = account.name, document = account.document, balance = account.balance) }
        return ResponseEntity.ok(accountList)
    }
}