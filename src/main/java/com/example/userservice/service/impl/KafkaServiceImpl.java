package com.example.userservice.service.impl;

import com.example.userservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String key, String message) {
        log.info("{} sendMessage message {}",getClass().getSimpleName(), message);
        kafkaTemplate.send(topic, key, message);
    }

    @Override
    public boolean createMessage() {
        log.info("{} createMessage start {}",getClass().getSimpleName(), System.currentTimeMillis());
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 10000; j++) {
                    sendMessage("order", "product" + j, "product " + j);
                }
            });
        }

        executor.shutdown();
        log.info("{} createMessage end {}",getClass().getSimpleName(), System.currentTimeMillis());
        return true;
    }
}
