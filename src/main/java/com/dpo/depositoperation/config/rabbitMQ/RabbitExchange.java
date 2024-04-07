package com.dpo.depositoperation.config.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitExchange {

    private String name;

    private boolean durable;

    private boolean autoDelete;

    private BuiltinExchangeType exchangeType;
}
