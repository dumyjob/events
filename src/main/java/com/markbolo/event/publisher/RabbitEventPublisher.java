package com.markbolo.event.publisher;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final MessageConverter messageConverter;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate,
                                MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            // 注意routingKey和exchange和topic/tag的区别
            String msg = messageConverter.format(message);
            rabbitTemplate.convertAndSend(topic, tag, msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
