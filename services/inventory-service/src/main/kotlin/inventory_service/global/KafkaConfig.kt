package inventory_service.global

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.converter.MessagingMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import tools.jackson.databind.json.JsonMapper
import java.lang.reflect.Type

@Configuration
class KafkaConfig {

    @Bean
    fun converter(jsonMapper : JsonMapper): RecordMessageConverter {
        return ToolsJacksonMessageConverter(jsonMapper)
    }
}

class ToolsJacksonMessageConverter(private val jsonMapper: JsonMapper) : MessagingMessageConverter() {
    override fun extractAndConvertValue(
        record: ConsumerRecord<*, *>, type: Type?): Any {

        val value = record.value()
        if (value is String && type != null) {
            try {
                return jsonMapper.readValue(value, jsonMapper.constructType(type))
            }catch (e : Exception) {
                throw RuntimeException("Failed deserialize JSON with Tools Jackson",e)
            }
        }

        return super.extractAndConvertValue(record, type)
    }
}