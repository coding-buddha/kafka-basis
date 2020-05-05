package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContainerReceiver {

    @KafkaListener(
            topics = "${kafka.consumer.container-topic}",
            containerFactory = "containerContainerFactory"
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