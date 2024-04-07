package com.dpo.depositoperation.config.rabbitMQ;

import com.dpo.depositoperation.config.rabbitMQ.observer.Observer;
import com.dpo.depositoperation.util.CommonUtil;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Slf4j
public class RetryQueuesInterceptor implements MethodInterceptor {

    private RabbitTemplate rabbitTemplate;

    private RetryQueues retryQueues;

    private Observer observer;

    public RetryQueuesInterceptor(RabbitTemplate rabbitTemplate, RetryQueues retryQueues) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryQueues = retryQueues;
    }

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        return tryConsume(invocation, this::ack, (mac, e) -> {
            try {
                int retryCount = tryGetRetryCountOrFail(mac, e);
                sendToNextRetryQueue(mac, retryCount, e);
            }
            catch (Throwable t) {
                if (observer != null) {
                    observer.run(mac.getMessage(), e);
                }
                throw new AmqpRejectAndDontRequeueException(t);
            }
        });
    }

    private Object tryConsume(MethodInvocation invocation, Consumer<MessageAndChannel> successHandler,
              BiConsumer<MessageAndChannel, Throwable> errorHandler) {

        MessageAndChannel mac = new MessageAndChannel(
                (Message) invocation.getArguments()[1],
                (Channel) invocation.getArguments()[0]);

        Object result = null;
        try {
            result = invocation.proceed();
            successHandler.accept(mac);
        } catch (Throwable e) {
            errorHandler.accept(mac, e.getCause().getCause());
        }

        return result;
    }

    private void ack(MessageAndChannel mac) {
        try {
            mac.getChannel().basicAck(mac.getMessage().getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int tryGetRetryCountOrFail(MessageAndChannel mac, Throwable originalError) {
        MessageProperties props = mac.getMessage().getMessageProperties();

        String xRetriedCountHeader = props.getHeader("x-retried-count");
        final int xRetriedCount = xRetriedCountHeader == null ? 0 : Integer.parseInt(xRetriedCountHeader);

        if (retryQueues.retriesExhausted(xRetriedCount)) {
            throw new AmqpRejectAndDontRequeueException(originalError);
        }

        return xRetriedCount;
    }

    private void sendToNextRetryQueue(MessageAndChannel mac, int retryCount, Throwable e) {
        String retryQueueName = retryQueues.getQueueName(retryCount);

        rabbitTemplate.convertAndSend(retryQueueName, mac.getMessage(), m -> {
            MessageProperties props = m.getMessageProperties();
            props.setExpiration(String.valueOf(retryQueues.getTimeToWait(retryCount)));
            props.setHeader("x-retried-count", String.valueOf(retryCount + 1));
            props.setHeader("x-original-exchange", props.getReceivedExchange());
            props.setHeader("x-original-routing-key", props.getReceivedRoutingKey());
            props.setHeader("x-stack-trace", CommonUtil.convertStackTraceToMessage(e));
            return m;
        });

        throw new AmqpRejectAndDontRequeueException(e);
    }
}
