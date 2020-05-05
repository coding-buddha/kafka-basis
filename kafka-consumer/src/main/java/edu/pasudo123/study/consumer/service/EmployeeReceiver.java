package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployeeReceiver {

    @KafkaListener(
            topics = "${kafka.consumer.employee-topic}",
            containerFactory = "employeeMessageKafkaListenerContainerFactory"
    )
    public void listen(List<Employee> employees) {
        log.info("====> Employees Size : [{}]", employees.size());
    }
}
