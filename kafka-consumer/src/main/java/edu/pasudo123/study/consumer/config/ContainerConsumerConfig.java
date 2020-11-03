package edu.pasudo123.study.consumer.config;

import edu.pasudo123.study.common.dto.Container;
import edu.pasudo123.study.consumer.props.CustomKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ContainerConsumerConfig {

    private final CustomKafkaProperties customKafkaProperties;

    @SuppressWarnings("Duplicates")
    @Bean(name = "containerConsumerFactory")
    public ConsumerFactory<String, Container> containerConsumerFactory() {
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
    public ConcurrentKafkaListenerContainerFactory<String, Container> containerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(containerConsumerFactory());

        // default 은 false 이다. :  true 이면 해당 레코드에서 문제 발생 시 해당레코드를 건너뛴다.
        // factory.getContainerProperties().setAckOnError(false);

        // 직접 ack 를 할 것인지 여부를 결정한다.
        // 그렇지 않으면, AcknowledgingMessageListener 를 사용한다. :: AckMode.MANUAL
        // throw 시 계속 에러 발생함.
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        // factory.setErrorHandler(new SeekToCurrentErrorHandler());

        return factory;
    }
}
