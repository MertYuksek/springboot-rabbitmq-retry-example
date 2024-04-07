package com.dpo.depositoperation.config.rabbitMQ.observer;

import org.springframework.amqp.core.Message;

public interface Observer {
    void run(Message message, Throwable throwable);
}
