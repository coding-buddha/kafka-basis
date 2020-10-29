package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerRetryReceiver {

    @KafkaListener(
            id = "container_retry_listener",
            topics = "${kafka.consumer.container.retry-topic}",
            containerFactory = "${kafka.consumer.container.container-retry-factory}"
    )
    public void retry(@Payload Container container) {
        log.info(">>>> [retry] [retry] [retry] ====================");
        log.info("container : {}", container);
    }
}
