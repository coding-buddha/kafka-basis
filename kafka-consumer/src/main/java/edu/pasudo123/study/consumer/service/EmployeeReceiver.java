package edu.pasudo123.study.consumer.service;

import edu.pasudo123.study.common.dto.Employee;
import edu.pasudo123.study.consumer.bean.CustomRetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmployeeReceiver {

    private final CustomRetry retry;

    @KafkaListener(
            id = "employee_listener",
            topics = "${kafka.consumer.employee.topic}",
            containerFactory = "${kafka.consumer.employee.container-factory}"
    )
    public void listen(@Payload List<Employee> employees) {
        log.info("====> Employees Size : [{}]", employees.size());
        employees.forEach(employee -> {
            log.info("==> Employee[{}] : {} ({}:{}:{})",
                    employee.getCurrentNumber(),
                    employee.getName(),
                    employee.getHh(),
                    employee.getMm(),
                    employee.getSs());

        });

        validate();
    }

    public void validate() {
        if(!retry.isBatchRetry()) {
//            retry.retrySuccess();
//            throw new RuntimeException();
        }
    }
}
