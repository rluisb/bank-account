package com.rluisb.bankaccount.exception

import com.rluisb.bankaccount.exception.custom.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors

import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ResponseStatus


@ControllerAdvice
class ControllerAdviceRequestError : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(InvalidDocumentException::class)])
    fun handleInvalidDocumentException(ex: InvalidDocumentException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Document validation failed",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(DocumentAlreadyExistsException::class)])
    fun handleDocumentAlreadyExistsException(ex: DocumentAlreadyExistsException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Document validation failed",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(value = [(AccountNotFoundException::class)])
    fun handleAccountNotFoundException(ex: AccountNotFoundException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Account Not Found",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(value = [(InvalidValueForDepositException::class)])
    fun handleInvalidValueForDepositException(ex: InvalidValueForDepositException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Invalid value for deposit",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(value = [(InvalidValueForTransferException::class)])
    fun handleInvalidValueForTransferException(ex: InvalidValueForTransferException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            message = "Invalid value for transfer",
            details = ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorDetails = ErrorsDetails(
            message = "Invalid request arguments",
            details = ex
                .bindingResult
                .fieldErrors
                .stream()
                .map { fieldError: FieldError -> fieldError.defaultMessage }
                .collect(Collectors.toList())
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

}