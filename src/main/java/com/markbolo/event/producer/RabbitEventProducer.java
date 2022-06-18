package com.markbolo.event.producer;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitEventProducer implements EventProducer {

    private final RabbitTemplate rabbitTemplate;

    private final MessageConverter messageConverter;

    public RabbitEventProducer(RabbitTemplate rabbitTemplate,
                               MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            /*
            * 注意RabbitMq是使用的exchange/routingKey,并不是常用的topic/tag
            * 再使用的时候一定要将exchange的模式申请为topic模式
            * 对于其他消息组件支持的多tag模式, RabbitMq可以通过在Broker端配置routingKey的策略完成
             */
            String msg = messageConverter.format(message);
            rabbitTemplate.convertAndSend(topic, tag, msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
