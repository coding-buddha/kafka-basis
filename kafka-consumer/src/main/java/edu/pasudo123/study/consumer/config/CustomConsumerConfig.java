package edu.pasudo123.study.consumer.config;

import edu.pasudo123.study.common.container.Container;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class CustomConsumerConfig {

    private final CustomKafkaProperties customKafkaProperties;

    @Bean
    public ConsumerFactory<String, Container> consumerFactory() {
        final Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, customKafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, customKafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, customKafkaProperties.getOffset());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer 를 Container 객체에 대해서 수행하고 있음을 명시.
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(Container.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Container> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);
        return factory;
    }
}
