package inventory_service.global.error

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        log.error("BusinessException: {}", e.errorCode.msg)
        val response = ErrorResponse.of(e.errorCode)
        return ResponseEntity.status(e.errorCode.status).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Exception: ", e)
        val response = ErrorResponse.of(ErrorCode.INTERNAL_ERROR)
        return ResponseEntity.status(500).body(response)
    }
}
