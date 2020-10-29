package edu.pasudo123.study.consumer.retry;

import edu.pasudo123.study.common.dto.Container;
import edu.pasudo123.study.consumer.props.CustomKafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter.CONTEXT_RECORD;

/**
 * retry 를 위한 producer 설정
 */
@Slf4j
@Configuration
public class RetryProducerConfig {

    private final CustomKafkaProperties customKafkaProperties;
    private final ConsumerFactory<String, Container> containerConsumerFactory;

    public RetryProducerConfig(CustomKafkaProperties customKafkaProperties,
                               @Qualifier("containerConsumerFactory") ConsumerFactory<String, Container> containerConsumerFactory) {
        this.customKafkaProperties = customKafkaProperties;
        this.containerConsumerFactory = containerConsumerFactory;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, customKafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "containerRetryKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Retry 전용 컨슈머 리스너 팩토리
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Container> containerContainerRetryFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(containerConsumerFactory);

        final KafkaTemplate<String, String> kafkaTemplate = kafkaTemplate();

        /**
         * retryTemplate 를 등록하고, retry 가 다 소모되면 이후에 callback 을 수행한다.
         */
        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback(retryContext -> {

            log.error("recovery(1) >>>>> {}", retryContext);
            log.error("recovery(2) >>>>> {}", (ConsumerRecord) retryContext.getAttribute(CONTEXT_RECORD));

            ConsumerRecord record = (ConsumerRecord) retryContext.getAttribute(CONTEXT_RECORD);
            String errorTopic = record.topic().replace("RETRY", "ERROR");
            Container container = (Container) record.value();

            final Message<Container> message = MessageBuilder
                    .withPayload(container)
                    .setHeader(KafkaHeaders.TOPIC, errorTopic)
                    .build();

            kafkaTemplate.send(message);

            return Optional.empty();
        });

        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        final RetryTemplate retryTemplate = new RetryTemplate();
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();

        // 주어진 토픽 파티션에 대해 실패한 요청을 재시도하기 전에 대기하는 시간설정 (ms)
        // 일부 실패한 시나리오에 대해서 반복적으로 요청을 보내는것을 방지할 수 있다.
        fixedBackOffPolicy.setBackOffPeriod(3000L);

        // 재시도 횟수 설정
        // 3 : 초기(1회) + 재시도(2회)
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}
