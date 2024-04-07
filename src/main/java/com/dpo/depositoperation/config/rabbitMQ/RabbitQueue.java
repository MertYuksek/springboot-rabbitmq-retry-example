package com.dpo.depositoperation.config.rabbitMQ;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RabbitQueue {

    private String name;

    private boolean durable;

    private boolean exclusive;

    private boolean autoDelete;

    private Map<String, Object> arguments;
}
