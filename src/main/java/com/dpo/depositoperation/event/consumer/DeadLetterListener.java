package com.dpo.depositoperation.event.consumer;

import com.dpo.depositoperation.model.entitiy.DeadLetterMessage;
import com.dpo.depositoperation.model.repository.DeadLetterMessageRepository;
import com.rabbitmq.client.LongString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadLetterListener {
    private final RabbitTemplate rabbitTemplate;
    private final DeadLetterMessageRepository deadLetterMessageRepository;

    @RabbitListener(queues = "q.dlx.messages")
    public void consumeDeadLetterMessage(Message message) {
        saveConsumedMessageToDb(message);
    }

    @SuppressWarnings("unchecked")
    private void saveConsumedMessageToDb(Message message) {
        log.info("The message received by the dead letter queue has been saved to the database. -> " + new String(message.getBody()));
        var deathMap = ((List<Map<String, String>>) message.getMessageProperties().getHeaders().get("x-death")).get(0);
        DeadLetterMessage deadLetterMessage = deadLetterMessageRepository.findByCorrelationId(
                message.getMessageProperties().getCorrelationId()).orElse(new DeadLetterMessage());

        deadLetterMessage.setDate(LocalDateTime.now());
        deadLetterMessage.setQueue(deathMap.get("queue"));
        deadLetterMessage.setReason(getErrorMessage(message));
        deadLetterMessage.setExchange(deathMap.get("exchange"));
        deadLetterMessage.setMessage(new String(message.getBody()));
        deadLetterMessage.setCorrelationId(message.getMessageProperties().getCorrelationId());
        deadLetterMessageRepository.save(deadLetterMessage);
    }

    private String getErrorMessage(Message message) {
        Object stackTrace = message.getMessageProperties().getHeaders().get("x-stack-trace");
        if (Objects.nonNull(stackTrace)) {
            String error = new String(((LongString) message.getMessageProperties().getHeaders().get("x-stack-trace")).getBytes());
            return error.substring(0, Math.min(error.length(), 1900));
        }
        return (String) message.getMessageProperties().getHeaders().get("x-first-death-reason");
    }

    @RabbitListener(queues = "q.event.retryWaitEndedQueue")
    public void consumeRetryWaitEndedMessage(Message message) {
        log.info("The message received by the retry queue -> " + new String(message.getBody()));
        MessageProperties props = message.getMessageProperties();
        rabbitTemplate.convertAndSend(props.getHeader("x-original-exchange"),
                props.getHeader("x-original-routing-key"), message);
    }
}
