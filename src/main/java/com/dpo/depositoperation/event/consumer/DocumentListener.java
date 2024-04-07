package com.dpo.depositoperation.event.consumer;

import com.dpo.depositoperation.config.rabbitMQ.observer.ObserverFactory;
import com.dpo.depositoperation.dto.DocumentRequest;
import com.dpo.depositoperation.event.handler.DocumentOperationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableRetry
@RequiredArgsConstructor
public class DocumentListener {
    private final ObserverFactory observerFactory;
    private final DocumentOperationHandler documentOperationHandler;

    /**
     * Blocking way retrying with custom container factory
     **/
    //@RabbitListener(queues = "q.event.document", containerFactory = "customContainerFactory")
    public void blockingWithCustomContainer(DocumentRequest request, Message message) {
        String result = documentOperationHandler.handleMessage(request, message);
        log.info("Message process response : " + result);
    }

    /**
     * Blocking way retrying with spring retryable
     **/
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    //@RabbitListener(queues = "q.event.document")
    public void blockingWithRetryable(DocumentRequest request, Message message) {
        String result = documentOperationHandler.handleMessage(request, message);
        log.info("Message process response : " + result);
    }

    /**
     * Non-Blocking way retrying with constant backoff
     **/
    //@RabbitListener(queues = "q.event.document", containerFactory = "constantBackoffContainerFactory")
    public void nonBlockingWithConstantDelay(DocumentRequest request, Message message) {
        String result = documentOperationHandler.handleMessage(request, message);
        log.info("Message process response : " + result);
    }

    /**
     * Non-Blocking way retrying with exponential backoff
     **/
    @RabbitListener(queues = "q.event.document", containerFactory = "exponentialBackoffContainerFactory")
    public void nonBlockingWithExponentialBackoff(DocumentRequest request, Message message) {
        String result = documentOperationHandler.handleMessage(request, message);
        log.info("Message process response : " + result);
    }
}
