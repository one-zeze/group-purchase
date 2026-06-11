package inventory_service.service

import inventory_service.domain.Inventory
import inventory_service.global.error.BusinessException
import inventory_service.global.error.ErrorCode
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
            .orElseThrow { BusinessException(ErrorCode.PRODUCT_NOT_FOUND) }

        inventory.decrease(quantity)
        // JPA dirty checking will handle the update
    }

    @Transactional
    fun registStock(productId: String, quantity: Int) {
        if (inventoryRepository.existsById(productId)) {
            throw BusinessException(ErrorCode.STOCK_ALREADY_EXIST)
        }
        inventoryRepository.save(Inventory(productId, quantity))

    }


}
