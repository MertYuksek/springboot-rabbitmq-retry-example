package com.dpo.depositoperation.config.rabbitMQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryQueues {
    private String[] queues;
    private long initialInterval;
    private double factor;
    private long maxWait;

    public RetryQueues(long initialInterval, double factor, long maxWait, String... queues) {
        this.queues = queues;
        this.initialInterval = initialInterval;
        this.factor = factor;
        this.maxWait = maxWait;
    }

    public boolean retriesExhausted(int retry) {
        return retry >= queues.length;
    }

    public String getQueueName(int retry) {
        return queues[retry];
    }

    public long getTimeToWait(int retry) {
        double time = initialInterval * Math.pow(factor, retry);
        if (time > maxWait) {
            return maxWait;
        }
        return (long) time;
    }
}