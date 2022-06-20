package com.markbolo.event.consumer.adpater.alions;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;

public class AliOnsConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    private final AliyunOnsProperties aliyunOnsProperties;

    public AliOnsConsumerFactory(MessageConverter messageConverter,
                                 AliyunOnsProperties aliyunOnsProperties) {
        this.messageConverter = messageConverter;
        this.aliyunOnsProperties = aliyunOnsProperties;
    }

    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new AliOnsMessageConsumer<>(consumerProperty, messageConverter, consumer, aliyunOnsProperties);
    }
}
