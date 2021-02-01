package com.rluisb.bankaccount.exception

import com.rluisb.bankaccount.exception.custom.DocumentAlreadyExists
import com.rluisb.bankaccount.exception.custom.ErrorsDetails
import com.rluisb.bankaccount.exception.custom.InvalidDocument
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@ControllerAdvice
class ControllerAdviceRequestError : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(InvalidDocument::class)])
    fun handleInvalidDocument(ex: InvalidDocument,request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Document validation failed",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(DocumentAlreadyExists::class)])
    fun handleDocumentAlreadyExists(ex: DocumentAlreadyExists,request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Document validation failed",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY)
    }
}