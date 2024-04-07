package com.dpo.depositoperation.event.handler;

import com.dpo.depositoperation.dto.DocumentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DocumentOperationHandler extends QueueHandlerWithReplay<DocumentRequest, String> {

    @Override
    protected String processMessage(DocumentRequest request, Message message) {
        log.info("Message is consumed by Listener -> " + request);
        // Throw Error
        List<String> str = new ArrayList<>();
        System.out.println(str.get(0).toLowerCase());
        return "Message is consumed";
    }
}
