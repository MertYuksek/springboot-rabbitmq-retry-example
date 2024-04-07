package com.dpo.depositoperation.event.handler;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

public abstract class QueueHandlerWithReplay<I, O> {

    public O handleMessage(I request, Message message) {
        try {
            return processMessage(request, message);
        }
        catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    protected abstract O processMessage(I request, Message message);
}
