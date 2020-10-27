package edu.pasudo123.study.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication {

    // https://dzone.com/articles/spring-for-apache-kafka-deep-dive-part-1-error-han
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
