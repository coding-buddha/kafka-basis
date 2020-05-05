package edu.pasudo123.study.producer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka.producer")
public class CustomKafkaProperties {
    private String containerTopic;
    private String employeeTopic;
    private List<String> bootstrapServers;
}
