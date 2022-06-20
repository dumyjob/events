package com.markbolo.event.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitEventProducer implements EventProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String topic, String tag, String body) {
        try {
            /*
             * 注意RabbitMq是使用的exchange/routingKey,并不是常用的topic/tag
             * 再使用的时候一定要将exchange的模式申请为topic模式
             * 对于其他消息组件支持的多tag模式, RabbitMq可以通过在Broker端配置routingKey的策略完成
             */
            rabbitTemplate.convertAndSend(topic, tag, body);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
