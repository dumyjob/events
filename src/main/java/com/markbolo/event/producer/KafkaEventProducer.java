package com.markbolo.event.producer;

import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventProducer implements EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(String topic, String tag, String body) {
        try {
            // kafka的partition如何体现
            kafkaTemplate.send(topic, tag, body);
        } catch (Exception e) {
            throw new ProducerException("message produce exception:" + e.getMessage(), e);
        }

    }
}
