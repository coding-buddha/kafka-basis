package edu.pasudo123.study.config;

import edu.pasudo123.study.common.dto.Employee;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Profile("test")
@Configuration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestContainerConfig {

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Configuration
    @EnableAutoConfiguration
    public static class BaseProducerConfig {

        @Bean
        public ProducerFactory<String, Employee> employeeProducerFactory() {
            kafka.start();
            final Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

            return new DefaultKafkaProducerFactory<>(configProps);
        }
        @Bean("employeeKafkaTemplate")
        public KafkaTemplate<String, Employee> kafkaTemplate(){
            return new KafkaTemplate<>(employeeProducerFactory());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class BaseConsumerConfig {

        @Bean
        public ConsumerFactory<String, Employee> employeeConsumerFactory() {
            kafka.start();
            final Map<String, Object> configProps = new HashMap<>();
            configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "GROUP_EMP");
            configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

            configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);

            return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(Employee.class));
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Employee> employeeContainerFactory(){
            ConcurrentKafkaListenerContainerFactory<String, Employee> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(employeeConsumerFactory());
            return factory;
        }
    }
}
