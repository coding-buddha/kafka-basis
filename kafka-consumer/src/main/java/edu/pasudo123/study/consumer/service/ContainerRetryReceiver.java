package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
    public void retry(@Payload Container container,
                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                      @Header(KafkaHeaders.OFFSET) String offset) {
        log.info("");
        log.info(">>>> [retry] [retry] [retry] [retry] [retry] <<<<");
        log.info("topic : {}", topic);
        log.info("container : {}", container);

        // 4 의 배수인 경웨 에러를 발생시킨다.
        // 따로 catch 문을 잡지 않아야 retry 가 가능하다.
        if (container.getCurrentNumber() % 4 == 0) {
            throw new RuntimeException("의도적 에러발생");
        }
    }
}
