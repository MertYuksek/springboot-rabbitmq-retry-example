package com.dpo.depositoperation.config.rabbitMQ.observer;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

@Setter
@RequiredArgsConstructor
public class ObservableRejectAndDontRequeue extends RejectAndDontRequeueRecoverer {
    private final Observer observer;

    @Override
    public void recover(Message message, Throwable cause) {
        if(observer != null) {
            observer.run(message, cause);
        }

        super.recover(message, cause);
    }
}
