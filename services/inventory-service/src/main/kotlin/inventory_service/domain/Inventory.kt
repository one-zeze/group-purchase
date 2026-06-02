package inventory_service.domain

import inventory_service.global.error.BusinessException
import inventory_service.global.error.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "inventory")
class Inventory(
    @Id
    @Column(name = "product_id", length = 36)
    val productId: String,

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()
) {
    fun decrease(quantity: Int) {
        if (quantity < 0) {
            throw BusinessException(ErrorCode.INVALID_QUANTITY)
        }
        if (this.stockQuantity < quantity) {
            throw BusinessException(ErrorCode.INSUFFICIENT_STOCK)
        }
        this.stockQuantity -= quantity
        this.updatedAt = Instant.now()
    }
}
