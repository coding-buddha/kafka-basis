package edu.pasudo123.study.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pasudo123.study.common.dto.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContainerReceiver {

    @Value("${kafka.consumer.container.retry-topic}")
    private String retryTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public ContainerReceiver(@Qualifier("containerRetryKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
                             ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @KafkaListener(
            id = "container_listener",
            topics = "${kafka.consumer.container.topic}",
            containerFactory = "${kafka.consumer.container.container-factory}"
    )
    public void listen(@Payload Container container,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) String offset) {

        log.info("=========================================");
        log.info("=> topic : {}", topic);
        log.info("=> offset : {}", offset);

        try {
            log.info("==> Container[{}] : {} ({}:{}:{})",
                    container.getCurrentNumber(),
                    container.getName(),
                    container.getHh(),
                    container.getMm(),
                    container.getSs());

            if (container.getCurrentNumber() % 2 == 0) {
                throw new RuntimeException("짝수 번호에서만 에러발생");
            }
        } catch (Exception e) {
            // exception 발생 시, retry 할 수 있도록 한다.
            // TOPIC_NAME + GROUP_ID + RETRY
            log.error("==> retry 를 수행할 수 있도록 한다.");
            log.error("==> error : {}", e.getMessage());

            String payload = "";

            // json 으로 한 번 변환 이후에 작업을 수행.
            try {
                payload = mapper.writeValueAsString(container);
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Json processing error : {}", container.toString());
                return;
            }

            // error exception 시 재시도 토픽에 데이터를 삽입한다.
            // 리텐션 기간 설정, 파티션 설정, 토픽 설정
            final Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, retryTopic)
                    .build();

            kafkaTemplate.send(message);
        }
    }
}
