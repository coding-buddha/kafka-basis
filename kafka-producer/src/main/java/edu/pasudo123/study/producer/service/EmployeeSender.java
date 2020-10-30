package edu.pasudo123.study.producer.service;

import edu.pasudo123.study.common.dto.Container;
import edu.pasudo123.study.common.dto.Employee;
import edu.pasudo123.study.producer.config.CustomKafkaProperties;
import edu.pasudo123.study.producer.pojo.AtomicNumber;
import edu.pasudo123.study.producer.pojo.NameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSender {

    private final NameGenerator nameGenerator;
    private final AtomicNumber number;
    private final CustomKafkaProperties customProps;

    @Qualifier("employeeKafkaTemplate")
    private final KafkaTemplate<String, Employee> kafkaTemplate;

    public void send(Employee employee) {

        log.info("[Employee] Numbering : {}, 시간 : {}", employee.getCurrentNumber(), LocalDateTime.now());

        final Message<Employee> message = MessageBuilder
                .withPayload(employee)
                .setHeader(KafkaHeaders.TOPIC, customProps.getEmployeeTopic())
                .setHeader(KafkaHeaders.MESSAGE_KEY, employee.getName())
                .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, employee.getName())
                .build();

        kafkaTemplate.send(message);
    }

//    @Scheduled(cron = "*/20 * * * * *")
//    public void schedulingTask(){
//        final LocalTime currentTime = LocalTime.now();
//
//        final Employee employee = Employee.builder()
//                .currentNumber(number.getCurrentNumber())
//                .name(nameGenerator.getName())
//                .hh(currentTime.getHour())
//                .mm(currentTime.getMinute())
//                .ss(currentTime.getSecond())
//                .build();
//
//        send(employee);
//    }
}
