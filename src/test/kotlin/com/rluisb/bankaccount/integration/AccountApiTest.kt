package com.rluisb.bankaccount.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.request.DepositRequest
import com.rluisb.bankaccount.api.account.dto.request.TransferRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.service.AccountService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
internal class AccountApiTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
){
    private val baseUrl: String = "/account"

    @Nested
    @DisplayName("GET /api/bank/account")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class ListAccounts {
        @Test
        fun `should return a list with one item`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "187.958.930-32")

            val stringPostResponse = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }.andReturn().response.contentAsString

            val postResponse = objectMapper.readValue<AccountResponse>(stringPostResponse)

            mockMvc.get(baseUrl)
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON)}

                    jsonPath("$[0].accountNumber") { postResponse.accountNumber }
                    jsonPath("$[0].name") { value(postResponse.name) }
                    jsonPath("$[0].document") { value(postResponse.document) }
                    jsonPath("$[0].balance") { value(postResponse.balance) }

                }
        }
    }

    @Nested
    @DisplayName("POST /api/bank/account")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class CreateAccount {
        @Test
        fun `should create new account successfully by using name and document`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "044.003.350-03")

            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }

            performPost
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON)}
                    jsonPath("$.accountNumber") { isNotEmpty() }
                    jsonPath("$.name") { value(accountRequest.name) }
                    jsonPath("$.document") { value(accountRequest.document) }
                    jsonPath("$.balance") { value(0) }
                }
        }

        @Test
        fun `should not create an account because document is invalid`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "044.003.350-02")

            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }

            performPost
                .andExpect {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON)}
                    jsonPath("$.time") { isNotEmpty() }
                    jsonPath("$.message") { value("Document validation failed") }
                    jsonPath("$.details") { value("Document ${accountRequest.document} invalid. It must be a valid CPF. Ex.: 999.999.999-99") }

                }
        }
    }


    @Nested
    @DisplayName("PATCH /api/bank/account/{accountNumber}/deposit")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class Deposit {
        @Test
        fun `should update balance into account successfully`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "183.296.040-47")

            val accountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(accountRequest.name) }
                jsonPath("$.document") { value(accountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val accountResponse = objectMapper.readValue(accountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = 100L)

            mockMvc.patch("$baseUrl/${accountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(accountRequest.name) }
                jsonPath("$.document") { value(accountRequest.document) }
                jsonPath("$.balance") { value(depositRequest.amount) }
            }
        }

        @Test
        fun `should return error because amount is negative`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "595.848.830-97")

            val accountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(accountRequest.name) }
                jsonPath("$.document") { value(accountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val accountResponse = objectMapper.readValue(accountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = -100L)

            mockMvc.patch("$baseUrl/${accountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isUnprocessableEntity() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.time") { isNotEmpty() }
                jsonPath("$.message") { value("Invalid value for deposit") }
                jsonPath("$.details") { value("Value ${depositRequest.amount} cannot be negative.") }
            }
        }

        @Test
        fun `should return error because amount is greater than limit for deposit`() {
            val accountRequest = AccountRequest(name = "John Doe", document = "663.293.290-87")

            val accountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(accountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(accountRequest.name) }
                jsonPath("$.document") { value(accountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val accountResponse = objectMapper.readValue(accountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = 3000L)

            mockMvc.patch("$baseUrl/${accountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isUnprocessableEntity() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.time") { isNotEmpty() }
                jsonPath("$.message") { value("Invalid value for deposit") }
                jsonPath("$.details") { value("Value ${depositRequest.amount} cannot exceed the security limit of ${AccountService.MAX_LIMIT_FOR_DEPOSIT_OPERATION}.") }
            }
        }
    }

    @Nested
    @DisplayName("PATCH /api/bank/account/{accountNumber}/transfer")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class Transfer {
        @Test
        fun `should transfer amount from one account to another account`() {
            val originAccountRequest = AccountRequest(name = "John Doe", document = "862.874.890-30")

            val originAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(originAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountRequest.name) }
                jsonPath("$.document") { value(originAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val originAccountResponse = objectMapper.readValue(originAccountResponseByteArray, AccountResponse::class.java)

            val targetAccountRequest = AccountRequest(name = "Foo Bar", document = "703.522.340-16")

            val targetAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(targetAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(targetAccountRequest.name) }
                jsonPath("$.document") { value(targetAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val targetAccountResponse = objectMapper.readValue(targetAccountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = 500L)

            val originAccountWithUpdatedBalanceByteArray = mockMvc.patch("$baseUrl/${originAccountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountResponse.name) }
                jsonPath("$.document") { value(originAccountResponse.document) }
                jsonPath("$.balance") { value(depositRequest.amount) }
            }.andReturn().response.contentAsByteArray

            val originAccountWithUpdatedBalance = objectMapper.readValue(originAccountWithUpdatedBalanceByteArray, AccountResponse::class.java)

            val transferRequest = TransferRequest(
                targetAccountNumber = targetAccountResponse.accountNumber!!,
                amount = 250L
            )

            mockMvc.patch("$baseUrl/${originAccountWithUpdatedBalance.accountNumber}/transfer") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(transferRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { originAccountWithUpdatedBalance.accountNumber }
                jsonPath("$.name") { value(originAccountWithUpdatedBalance.name) }
                jsonPath("$.document") { value(originAccountWithUpdatedBalance.document) }
                jsonPath("$.balance") { value(originAccountWithUpdatedBalance.balance.minus(transferRequest.amount)) }
            }

            mockMvc.get("$baseUrl/${targetAccountResponse.accountNumber}")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON)}

                    jsonPath("$.accountNumber") { targetAccountResponse.accountNumber }
                    jsonPath("$.name") { value(targetAccountResponse.name) }
                    jsonPath("$.document") { value(targetAccountResponse.document) }
                    jsonPath("$.balance") { value(transferRequest.amount) }

                }
        }

        @Test
        fun `should return error for negative amount for transfer`() {
            val originAccountRequest = AccountRequest(name = "John Doe", document = "560.626.080-83")

            val originAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(originAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountRequest.name) }
                jsonPath("$.document") { value(originAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val originAccountResponse = objectMapper.readValue(originAccountResponseByteArray, AccountResponse::class.java)

            val targetAccountRequest = AccountRequest(name = "Foo Bar", document = "334.528.600-94")

            val targetAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(targetAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(targetAccountRequest.name) }
                jsonPath("$.document") { value(targetAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val targetAccountResponse = objectMapper.readValue(targetAccountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = 500L)

            val originAccountWithUpdatedBalanceByteArray = mockMvc.patch("$baseUrl/${originAccountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountResponse.name) }
                jsonPath("$.document") { value(originAccountResponse.document) }
                jsonPath("$.balance") { value(depositRequest.amount) }
            }.andReturn().response.contentAsByteArray

            val originAccountWithUpdatedBalance = objectMapper.readValue(originAccountWithUpdatedBalanceByteArray, AccountResponse::class.java)

            val transferRequest = TransferRequest(
                targetAccountNumber = targetAccountResponse.accountNumber!!,
                amount = -1L
            )

            mockMvc.patch("$baseUrl/${originAccountWithUpdatedBalance.accountNumber}/transfer") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(transferRequest)
            }.andExpect {
                status { isUnprocessableEntity() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.time") { isNotEmpty() }
                jsonPath("$.message") { value("Invalid value for transfer") }
                jsonPath("$.details") { value("Value ${transferRequest.amount} cannot be negative.") }
            }
        }

        @Test
        fun `should return error because it can't turn origin account balance negative`() {
            val originAccountRequest = AccountRequest(name = "John Doe", document = "632.361.000-01")

            val originAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(originAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountRequest.name) }
                jsonPath("$.document") { value(originAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val originAccountResponse = objectMapper.readValue(originAccountResponseByteArray, AccountResponse::class.java)

            val targetAccountRequest = AccountRequest(name = "Foo Bar", document = "785.808.200-06")

            val targetAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(targetAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(targetAccountRequest.name) }
                jsonPath("$.document") { value(targetAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val targetAccountResponse = objectMapper.readValue(targetAccountResponseByteArray, AccountResponse::class.java)

            val depositRequest = DepositRequest(amount = 500L)

            val originAccountWithUpdatedBalanceByteArray = mockMvc.patch("$baseUrl/${originAccountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountResponse.name) }
                jsonPath("$.document") { value(originAccountResponse.document) }
                jsonPath("$.balance") { value(depositRequest.amount) }
            }.andReturn().response.contentAsByteArray

            val originAccountWithUpdatedBalance = objectMapper.readValue(originAccountWithUpdatedBalanceByteArray, AccountResponse::class.java)

            val transferRequest = TransferRequest(
                targetAccountNumber = targetAccountResponse.accountNumber!!,
                amount = 600L
            )

            mockMvc.patch("$baseUrl/${originAccountWithUpdatedBalance.accountNumber}/transfer") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(transferRequest)
            }.andExpect {
                status { isUnprocessableEntity() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.time") { isNotEmpty() }
                jsonPath("$.message") { value("Invalid value for transfer") }
                jsonPath("$.details") { value("Value for transfer cannot turn originAccount balance negative. Balance after transfer will be: ${originAccountWithUpdatedBalance.balance.minus(transferRequest.amount)}") }
            }
        }

        @Test
        fun `should return error because target account can't be equal to origin account`() {
            val originAccountRequest = AccountRequest(name = "John Doe", document = "616.931.250-56")

            val originAccountResponseByteArray = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(originAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountRequest.name) }
                jsonPath("$.document") { value(originAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }.andReturn().response.contentAsByteArray

            val originAccountResponse = objectMapper.readValue(originAccountResponseByteArray, AccountResponse::class.java)

            val targetAccountRequest = AccountRequest(name = "Foo Bar", document = "541.290.090-95")

            mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(targetAccountRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(targetAccountRequest.name) }
                jsonPath("$.document") { value(targetAccountRequest.document) }
                jsonPath("$.balance") { value(0) }
            }

            val depositRequest = DepositRequest(amount = 500L)

            val originAccountWithUpdatedBalanceByteArray = mockMvc.patch("$baseUrl/${originAccountResponse.accountNumber}/deposit") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(depositRequest)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.accountNumber") { isNotEmpty() }
                jsonPath("$.name") { value(originAccountResponse.name) }
                jsonPath("$.document") { value(originAccountResponse.document) }
                jsonPath("$.balance") { value(depositRequest.amount) }
            }.andReturn().response.contentAsByteArray

            val originAccountWithUpdatedBalance = objectMapper.readValue(originAccountWithUpdatedBalanceByteArray, AccountResponse::class.java)

            val transferRequest = TransferRequest(
                targetAccountNumber = originAccountWithUpdatedBalance.accountNumber!!,
                amount = 600L
            )

            mockMvc.patch("$baseUrl/${originAccountWithUpdatedBalance.accountNumber}/transfer") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(transferRequest)
            }.andExpect {
                status { isUnprocessableEntity() }
                content { contentType(MediaType.APPLICATION_JSON)}
                jsonPath("$.time") { isNotEmpty() }
                jsonPath("$.message") { value("Invalid target account for transfer") }
                jsonPath("$.details") { value("Target account: ${transferRequest.targetAccountNumber} cannot be equal to Origin account: ${originAccountWithUpdatedBalance.accountNumber}") }
            }
        }

    }
}