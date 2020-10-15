package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployeeReceiver {

    @KafkaListener(
            id = "employee_listener",
            topics = "${kafka.consumer.employee-topic}",
            containerFactory = "employeeContainerFactory"
    )
    public void listen(@Payload List<Employee> employees) {
        employees.forEach(employee -> {
            log.info("==> Employee[{}] : {} ({}:{}:{})",
                    employee.getCurrentNumber(),
                    employee.getName(),
                    employee.getHh(),
                    employee.getMm(),
                    employee.getSs());
        });

        log.info("====> Employees Size : [{}]", employees.size());
    }
}
