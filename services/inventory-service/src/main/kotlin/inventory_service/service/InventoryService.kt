package inventory_service.service

import inventory_service.repository.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository
) {

    @Transactional
    fun decreaseStock(productId: String, quantity: Int) {
        val inventory = inventoryRepository.findByProductIdWithLock(productId)
            .orElseThrow { IllegalArgumentException("상품 재고 정보를 찾을 수 없습니다: $productId") }

        inventory.decrease(quantity)
        // JPA dirty checking will handle the update
    }
}
