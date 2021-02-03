package com.rluisb.bankaccount

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import com.rluisb.bankaccount.api.account.dto.response.AccountResponse
import com.rluisb.bankaccount.exception.custom.ErrorsDetails
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.ImmutableMongoRestoreConfig
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.runtime.AbstractMongo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
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






}