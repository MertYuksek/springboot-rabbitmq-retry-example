package com.dpo.depositoperation.config.rabbitMQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitBinding {

    private String queueName;

    private String exchangeName;

    private String routingKey;
}
