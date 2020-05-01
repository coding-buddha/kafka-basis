package edu.pasudo123.study.producer.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "kafka.producer")
public class CustomKafkaProperties {
    private String topic;
    private List<String> bootstrapServers;
}
