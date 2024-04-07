package com.dpo.depositoperation.config.rabbitMQ;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rabbitconfig")
public class RabbitPublisherConfig {

    private Map<String, RabbitQueue> queues;
    private Map<String, RabbitExchange> exchanges;
    private Map<String, RabbitBinding> bindings;

    @Bean
    public ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/dev");
        connectionFactory.setUsername("dev");
        connectionFactory.setPassword("dev");
        Channel channel = getChannel(connectionFactory);
        validateDefinitions();
        createQueues(channel);
        createExchanges(channel);
        createBindings(channel);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(ConnectionFactory connectionFactory) {
        return new AsyncRabbitTemplate(rabbitTemplate(connectionFactory));
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private void createQueues(Channel channel) {
        queues.forEach((key, queue) -> {
            if (!isQueueExist(queue, channel)) {
                createQueue(queue, channel);
            }
        });
    }

    private void createExchanges(Channel channel) {
        exchanges.forEach((key, exchange) -> {
            if (!isExchangeExist(exchange, channel)) {
                createExchange(exchange, channel);
            }
        });
    }

    private void createBindings(Channel channel) {
        bindings.forEach((key, binding) -> {
            try {
                channel.queueBind(binding.getQueueName(), binding.getExchangeName(), binding.getRoutingKey());
                log.info("Queue: { " + binding.getQueueName() + " } and Exchange: { " + binding.getExchangeName() +
                        " }," + " is connected with Routing Key: { " + binding.getRoutingKey() + " }" );
            } catch (IOException e) {
                log.error("An error occurred when binding Queue: { " + binding.getQueueName() + " } " +
                        "and Exchange: { " + binding.getExchangeName() + " }");
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isExchangeExist(RabbitExchange exchange, Channel channel) {
        try {
            channel.exchangeDeclarePassive(exchange.getName());
            log.info("Exchange: { " + exchange.getName() + " }" + " already exist.");
            return true;
        } catch (IOException e) {
            log.info("Exchange: { " + exchange.getName() + " }" + " is not exist.");
            return false;
        }
    }

    private void createExchange(RabbitExchange exchange, Channel channel) {
        try {
            channel.exchangeDeclare(exchange.getName(), exchange.getExchangeType(),
                    exchange.isDurable(), exchange.isAutoDelete(), null);
            log.info("Exchange: { " + exchange.getName() + " }" + " is created.");
        } catch (IOException e) {
            log.info("An error occurred when declaring " + "Exchange: { " + exchange.getName() + " }");
            throw new RuntimeException(e);
        }
    }

    private boolean isQueueExist(RabbitQueue queue, Channel channel) {
        try {
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queue.getName());
            log.info("Queue: { " + queue.getName() + " }" + " already exist.");
            log.info("Queue: { " + queue.getName() + " }" +
                    ", ready message count: " + declareOk.getMessageCount() +
                    ", consumer count: " + declareOk.getConsumerCount());
            return true;
        } catch (IOException e) {
            log.info("Queue: { " + queue.getName() + " }" + " is not exist.");
            return false;
        }
    }

    private void createQueue(RabbitQueue queue, Channel channel) {
        try {
            channel.queueDeclare(queue.getName(),queue.isDurable(),
                    queue.isExclusive(), queue.isAutoDelete(), queue.getArguments());
            log.info("{ " + queue.getName() + " }" + " is created.");
        } catch (IOException e) {
            log.info("An error occurred when declaring " + "{ " + queue.getName() + " }");
            throw new RuntimeException(e);
        }
    }

    private Channel getChannel(ConnectionFactory connectionFactory) {
        try(Connection connection = connectionFactory.createConnection()) {
            return connection.createChannel(true);
        }
    }

    private void validateDefinitions() {
        if (Objects.isNull(queues)) {
            throw new IllegalArgumentException("Queues must not be null");
        }
        if (Objects.isNull(exchanges)) {
            throw new IllegalArgumentException("Exchanges must not be null");
        }
        if (Objects.isNull(bindings)) {
            throw new IllegalArgumentException("Bindings must not be null");
        }
    }
}
