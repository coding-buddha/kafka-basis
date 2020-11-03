package edu.pasudo123.study.consumer.recovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter.CONTEXT_RECORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedMessageRecoverService {

    private final ObjectMapper mapper;

    public Optional<Object> recovery(RetryContext context) {

        ConsumerRecord record = (ConsumerRecord) context.getAttribute(CONTEXT_RECORD);

        try {
            String json = mapper.writeValueAsString(record.value());
            log.error("===============================================");
            log.error("============ [recovery] [recovery] ============");
            log.error(json);
        } catch (JsonProcessingException e) {
            log.error("Json Processing ERROR : {}", e.getMessage());
            return Optional.empty();
        }

        return Optional.empty();
    }
}
