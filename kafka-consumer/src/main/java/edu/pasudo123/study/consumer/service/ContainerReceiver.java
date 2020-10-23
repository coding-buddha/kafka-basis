package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContainerReceiver {

    @KafkaListener(
            id = "container_listener",
            topics = "${kafka.consumer.container.topic}",
            containerFactory = "${kafka.consumer.container.container-factory}"
    )
    public void listen(@Payload Container container) {


        log.info("==> Container[{}] : {} ({}:{}:{})",
                container.getCurrentNumber(),
                container.getName(),
                container.getHh(),
                container.getMm(),
                container.getSs());
    }
}