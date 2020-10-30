package edu.pasudo123.study.consumer.retry;

import edu.pasudo123.study.common.dto.Container;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter.CONTEXT_RECORD;

@Slf4j
@Configuration
public class RetryConsumerConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, Container> containerConsumerFactory;

    public RetryConsumerConfig(KafkaTemplate<String, String> kafkaTemplate,
                               @Qualifier("containerConsumerFactory") ConsumerFactory<String, Container> containerConsumerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.containerConsumerFactory = containerConsumerFactory;
    }

    /**
     * Retry 전용 컨슈머 리스너 팩토리
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Container> containerContainerRetryFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(containerConsumerFactory);
        factory.setErrorHandler(new SeekToCurrentErrorHandler());
        /**
         * retryTemplate 를 등록하고, retry 가 다 소모되면 이후에 callback 을 수행한다.
         */
        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback(retryContext -> {

            ConsumerRecord record = (ConsumerRecord) retryContext.getAttribute(CONTEXT_RECORD);
            log.info(":: [recovery] [recovery] [recovery] ::");
            log.info(":: recovery (1) >>>>> {}", retryContext);
            log.info(":: recovery (2) >>>>> {}", record);
            log.info(":: recovery (3) >>>>> {}", record.value());

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
        fixedBackOffPolicy.setBackOffPeriod(2000L);

        // 재시도 횟수 설정
        // 3 : 초기(1회) + 재시도(2회) = 토탈(3회)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, Collections.singletonMap(Exception.class, true));

        // retryTemplate 설정
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
