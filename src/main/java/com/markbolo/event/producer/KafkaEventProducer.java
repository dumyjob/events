package com.markbolo.event.producer;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventProducer implements EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final MessageConverter messageConverter;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                              MessageConverter messageConverter) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageConverter = messageConverter;
    }


    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            // kafka的partition如何体现
            String msg = messageConverter.format(message);
            kafkaTemplate.send(topic, tag, msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }

    }
}
