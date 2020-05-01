package edu.pasudo123.study.producer.pojo;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AtomicNumber {
    private final AtomicLong number = new AtomicLong(0);

    public boolean isMultiple50(){
        return (number.get() % 50L == 0);
    }

    public synchronized long getCurrentNumber(){
        return number.addAndGet(1);
    }
}
