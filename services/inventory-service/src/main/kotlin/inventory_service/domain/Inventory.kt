package inventory_service.domain

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
        if (this.stockQuantity < quantity) {
            throw IllegalArgumentException("재고가 부족합니다. (현재: ${this.stockQuantity}, 요청: $quantity)")
        }
        this.stockQuantity -= quantity
        this.updatedAt = Instant.now()
    }
}
