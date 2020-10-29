package edu.pasudo123.study.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication {

    // https://docs.spring.io/spring-kafka/reference/html/#thread-safety
    // https://dzone.com/articles/spring-for-apache-kafka-deep-dive-part-1-error-han
    // https://medium.com/trendyol-tech/how-to-implement-retry-logic-with-spring-kafka-710b51501ce2
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
