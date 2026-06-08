package inventory_service.global.error

data class ErrorResponse(
    val status: Int,
    val message: String,
    val code: String
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status,
                message = errorCode.msg,
                code = errorCode.name
            )
        }
    }
}
