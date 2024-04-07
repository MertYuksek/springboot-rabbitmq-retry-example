package com.dpo.depositoperation.event.publisher;

import com.dpo.depositoperation.dto.DocumentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishToDocumentQueue(DocumentRequest message) {
        UUID correlationId  = UUID.randomUUID();
        rabbitTemplate.convertAndSend("exc.document", "rk-queue1-exchange1",
            message, messageValue -> {
                var messageProperties = messageValue.getMessageProperties();
                messageProperties.setCorrelationId(correlationId.toString());
                return messageValue;
        });
    }
}
