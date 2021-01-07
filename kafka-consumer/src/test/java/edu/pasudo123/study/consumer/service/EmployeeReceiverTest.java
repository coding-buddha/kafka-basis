package edu.pasudo123.study.consumer.service;


import edu.pasudo123.study.common.dto.Employee;
import edu.pasudo123.study.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * https://www.testcontainers.org/modules/kafka/#zookeeper
 *
 * Confluent Platform 버전과 Kafka 버전 간의 호환
 *  - https://docs.confluent.io/platform/current/installation/versions-interoperability.html
 *
 * KafkaClient (SpringBoot) Compatibility
 *  - https://spring.io/projects/spring-kafka
 *
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        EmployeeReceiver.class
})
@Import(TestContainerConfig.class)
class EmployeeReceiverTest {

    @Autowired
    private EmployeeReceiver employeeReceiver;

    @Autowired
    @Qualifier("employeeKafkaTemplate")
    private KafkaTemplate<String, Employee> kafkaTemplate;

    @Test
    public void initTest() {

        // given
        final Employee employee = Employee.builder()
                .currentNumber(1L)
                .name("PARK")
                .hh(10)
                .mm(5)
                .ss(15)
                .build();

        final Message<Employee> message = MessageBuilder
                .withPayload(employee)
                .setHeader(KafkaHeaders.TOPIC, "TOPIC_EMP")
                .setHeader(KafkaHeaders.MESSAGE_KEY, employee.getName())
                .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, employee.getName())
                .build();

        kafkaTemplate.send(message);
    }
}