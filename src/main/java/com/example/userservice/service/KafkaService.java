package com.example.userservice.service;

public interface KafkaService {
    void sendMessage(String topic, String key, String message);
    boolean createMessage();
}
