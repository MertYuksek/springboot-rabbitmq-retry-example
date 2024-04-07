package com.dpo.depositoperation.config.rabbitMQ.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ConstantBackoffContainerObserver.BEAN_ID)
public class ConstantBackoffContainerObserver implements Observer {
    public static final String BEAN_ID = "ConstantBackoffContainerObserver";

    @Override
    public void run(Message message, Throwable throwable) {
       log.info("Consumer Throw Error: " + BEAN_ID + " -> " + throwable);
    }
}
