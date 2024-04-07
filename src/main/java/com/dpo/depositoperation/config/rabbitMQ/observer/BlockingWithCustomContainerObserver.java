package com.dpo.depositoperation.config.rabbitMQ.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component(BlockingWithCustomContainerObserver.BEAN_ID)
public class BlockingWithCustomContainerObserver implements Observer {
    public static final String BEAN_ID = "BlockingWithCustomContainerObserver";

    @Override
    public void run(Message message, Throwable throwable) {
        log.info("Consumer Throw Error: " + BEAN_ID + " -> " + throwable);
    }
}
