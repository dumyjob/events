package com.markbolo.event.consumer.registry;

import com.markbolo.event.consumer.ConsumerProcessor;
import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link com.markbolo.event.consumer.ConsumerProcessor}
 */
public class ConsumerProcessorConsumerFinder implements ConsumerFinder {

    private final ConsumerProperties consumerProperties;

    private final List<MessageConsumerFactory> messageConsumerFactories;

    private final List<ConsumerProcessor<?>> consumerProcessors;


    public ConsumerProcessorConsumerFinder(ConsumerProperties consumerProperties,
                                           List<MessageConsumerFactory> messageConsumerFactories,
                                           List<ConsumerProcessor<?>> consumerProcessors) {
        this.consumerProperties = consumerProperties;
        this.messageConsumerFactories = messageConsumerFactories;
        this.consumerProcessors = consumerProcessors;
    }

    @Override
    public List<MessageConsumer> findConsumers() {
        if (CollectionUtils.isEmpty(consumerProcessors)) {
            return Collections.emptyList();
        }

        List<MessageConsumer> consumers = new ArrayList<>();
        for (ConsumerProcessor<?> consumerProcessor : consumerProcessors) {
            String consumerName = consumerProcessor.name();
            ConsumerProperties.ConsumerConfiguration consumerConfiguration = consumerProperties.getConsumers()
                    .get(consumerName);
            ConsumerProperty consumerProperty = new ConsumerProperty(consumerName, consumerConfiguration);
            for (MessageConsumerFactory factory : messageConsumerFactories) {
                ConsumerHandler<?> consumerHandler = new ConsumerHandler<>(consumerProcessor::handle);
                MessageConsumer messageConsumerBean = factory.createConsumer(consumerProperty, consumerHandler);

                consumers.add(messageConsumerBean);
            }
        }

        return consumers;
    }
}
