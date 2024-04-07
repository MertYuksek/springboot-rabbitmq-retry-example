package com.dpo.depositoperation.config.rabbitMQ;

import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Message;

@Getter
@Setter
public class MessageAndChannel {
    private Message message;
    private Channel channel;

    public MessageAndChannel(Message message, Channel channel) {
        this.message = message;
        this.channel = channel;
    }
}
