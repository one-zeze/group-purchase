package inventory_service.global.error

enum class ErrorCode(val status: Int, val msg: String) {
    // server
    INTERNAL_ERROR(500, "서버에러"),

    // inventory
    PRODUCT_NOT_FOUND(404, "상품 재고 정보를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(400, "재고가 부족합니다."),
    INVALID_QUANTITY(400, "수량이 올바르지 않습니다."),
    STOCK_ALREADY_EXIST( 400, "이미 등록된 재고입니다.")
}
