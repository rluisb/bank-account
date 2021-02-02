package com.rluisb.bankaccount

import com.fasterxml.jackson.databind.ObjectMapper
import com.rluisb.bankaccount.api.account.dto.request.AccountRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
internal class AccountApiTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
){
    private val baseUrl: String = "/account"

    @Nested
    @DisplayName("GET /api/bank/account")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class ListAccounts {
        @Test
        fun `should return an empty list`() {
            mockMvc.get(baseUrl)
                .andDo {print()}
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON)}

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
                .andDo {print()}
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON)}
                    jsonPath("$.accountNumber") { isNotEmpty() }
                    jsonPath("$.name") { value(accountRequest.name) }
                    jsonPath("$.document") { value(accountRequest.document) }
                    jsonPath("$.balance") { value(0) }

                }
        }
    }





}