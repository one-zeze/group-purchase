package inventory_service.global.error

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.msg)
