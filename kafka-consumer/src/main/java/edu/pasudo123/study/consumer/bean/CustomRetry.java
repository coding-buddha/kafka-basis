package edu.pasudo123.study.consumer.bean;

import org.springframework.stereotype.Component;

@Component
public class CustomRetry {

    private boolean batchRetry;

    public boolean isBatchRetry() {
        return batchRetry;
    }

    public void retrySuccess() {
        this.batchRetry = true;
    }
}
