package groupbuy_service.global;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;


@Configuration
public class KafkaConfig {

    @Bean
    public RecordMessageConverter converter(JsonMapper jsonMapper) {
        return new ToolsJacksonMessageConverter(jsonMapper);
    }
}

class ToolsJacksonMessageConverter extends MessagingMessageConverter {
    private final JsonMapper jsonMapper;

    ToolsJacksonMessageConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, @Nullable Type type) {
        Object value = record.value();

        if (value instanceof String json && type != null) {
            try {
                return jsonMapper.readValue(json, jsonMapper.constructType(type));
            }catch (Exception e){
                throw new RuntimeException("Failed deserialize JSON with Tools Jackson", e);
            }
        }

        return super.extractAndConvertValue(record, type);
    }
}
