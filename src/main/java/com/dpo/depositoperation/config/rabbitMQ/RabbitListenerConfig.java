package com.dpo.depositoperation.config.rabbitMQ;

import com.dpo.depositoperation.config.rabbitMQ.observer.*;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * Default preFetchCount is 250 since 2.0
 */

@Configuration
public class RabbitListenerConfig implements RabbitListenerConfigurer {

    private final ObserverFactory observerFactory;
    private final RabbitTemplate rabbitTemplate;
    private final Jackson2JsonMessageConverter jsonMessageConverter;

    public RabbitListenerConfig(RabbitTemplate rabbitTemplate,
            Jackson2JsonMessageConverter jsonMessageConverter, ObserverFactory observerFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.jsonMessageConverter = jsonMessageConverter;
        this.observerFactory = observerFactory;
    }

    /**
     * Here, the default container factory is assigned to the Rabbit listener.
     **/
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        //registrar.setContainerFactory(defaultContainerFactory(connectionFactory));
    }

    /**
     * Blocking way retrying with custom container factory
     **/
    @Bean
    public SimpleRabbitListenerContainerFactory customContainerFactory(
            ConnectionFactory connectionFactory) {
        Advice[] adviceChain = { retryInterceptor() };
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAdviceChain(adviceChain);
        return factory;
    }

    /**
     * Non-Blocking way retrying with constant backoff
     **/
    @Bean
    public SimpleRabbitListenerContainerFactory constantBackoffContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        Advice[] adviceChain = { retryQueueInterceptor() };
        factory.setAdviceChain(adviceChain);
        return factory;
    }

    /**
     * Non-Blocking way retrying with exponential backoff
     **/
    @Bean
    public SimpleRabbitListenerContainerFactory exponentialBackoffContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        Advice[] adviceChain = { retryQueuesInterceptor() };
        factory.setAdviceChain(adviceChain);
        return factory;
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        Observer observer = observerFactory.getObserver(BlockingWithCustomContainerObserver.BEAN_ID);
        return RetryInterceptorBuilder.stateless()
                .backOffOptions(3000, 3.0, 30000)
                .maxAttempts(3)
                .recoverer(new ObservableRejectAndDontRequeue(observer))
                .build();
    }

    @Bean
    public RetryQueuesInterceptor retryQueueInterceptor() {
        var retryQueues = new RetryQueues(30000, 1, 30000,
                "q.event.retry1", "q.event.retry1", "q.event.retry1");
        RetryQueuesInterceptor retryQueuesInterceptor = new RetryQueuesInterceptor(rabbitTemplate, retryQueues);
        Observer observer = observerFactory.getObserver(ConstantBackoffContainerObserver.BEAN_ID);
        retryQueuesInterceptor.setObserver(observer);
        return retryQueuesInterceptor;
    }

    @Bean
    public RetryQueuesInterceptor retryQueuesInterceptor() {
        var retryQueues = new RetryQueues(1000, 3.0, 90000,
                "q.event.retry1", "q.event.retry2", "q.event.retry3");
        RetryQueuesInterceptor retryQueuesInterceptor = new RetryQueuesInterceptor(rabbitTemplate, retryQueues);
        Observer observer = observerFactory.getObserver(ExponentialBackoffContainerObserver.BEAN_ID);
        retryQueuesInterceptor.setObserver(observer);
        return retryQueuesInterceptor;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manuelAckContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
