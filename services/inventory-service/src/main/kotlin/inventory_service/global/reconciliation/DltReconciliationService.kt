package inventory_service.global.reconciliation

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper

@Service
class DltReconciliationService(
    private val failedEventRepository: FailedEventRepository,
    private val jsonMapper: JsonMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun logFailedEvent(topic: String, payload: Any, errorMessage: String) {
        failedEventRepository.save(
            FailedEvent(
                topic = topic,
                payload = serializePayload(payload),
                errorMessage = errorMessage
            )
        )
    }

    private fun serializePayload(payload: Any): String =
        try {
            jsonMapper.writeValueAsString(payload)
        } catch (e: JacksonException) {
            log.error("DLT payload serialization failed", e)
            FALLBACK_PAYLOAD
        }

    private companion object {
        const val FALLBACK_PAYLOAD = "Serialization failed"
    }
}
