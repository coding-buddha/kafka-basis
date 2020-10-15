package edu.pasudo123.study.consumer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import javax.annotation.PreDestroy;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaEnableConfig {

    private final KafkaListenerEndpointRegistry registry;

    @PreDestroy
    public void destroy() {
        log.info("pre-destroy");
        registry.getAllListenerContainers().forEach(messageListenerContainer -> {
            log.info("====================>");
            log.info("listener id : {}", messageListenerContainer.getListenerId());
            log.info("phase before : {}", messageListenerContainer.getPhase());
            messageListenerContainer.stop();
            log.info("phase after : {}", messageListenerContainer.getPhase());
        });
    }
}
