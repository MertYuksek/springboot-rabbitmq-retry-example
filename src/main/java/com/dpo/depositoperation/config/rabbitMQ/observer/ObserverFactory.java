package com.dpo.depositoperation.config.rabbitMQ.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ObserverFactory {
    private final Map<String, Observer> observerMap;

    public Observer getObserver(String observerName) {
        return observerMap.get(observerName);
    }
}