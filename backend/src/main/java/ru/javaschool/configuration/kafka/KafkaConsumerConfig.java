package ru.javaschool.configuration.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private static final String CONSUMER_GROUP_ID = "group-id";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<Long, ProcessingResultDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new LongDeserializer(),
                new JsonDeserializer<>(ProcessingResultDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, ProcessingResultDto> kafkaListenerContainerFactory(
            ConsumerFactory<Long, ProcessingResultDto> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, ProcessingResultDto>();
        factory.setConsumerFactory(consumerFactory);
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setSyncCommits(true);
        return factory;
    }
}
