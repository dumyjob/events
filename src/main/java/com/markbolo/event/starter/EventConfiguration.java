package com.markbolo.event.starter;

import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markbolo.event.EventPublisher;
import com.markbolo.event.JacksonMessageConverter;
import com.markbolo.event.MessageConverter;
import com.markbolo.event.producer.*;
import com.markbolo.event.scheduler.EventScheduler;
import com.markbolo.event.store.EventStore;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.TransactionManager;

@Configuration
@EnableLoadTimeWeaving
public class EventConfiguration {

    @Bean
    public EventPublisher eventProducer(EventStore eventStore) {
        return new EventPublisher(eventStore);
    }

    @Bean
    public EventScheduler eventScheduler(EventStore eventStore,
                                         EventProducer eventProducer) {
        return new EventScheduler(eventProducer, eventStore);
    }


    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MessageConverter jacksonMessageConverter() {
        return new JacksonMessageConverter();
    }

    @Bean
    public EventStore eventStore(TransactionManager transactionManager) {
//        return new RowsEventStore();
        // 需要TransactionManager
        return null;
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.rabbit")
    public EventProducer rabbitEventPublisher(RabbitTemplate rabbitTemplate,
                                              MessageConverter messageConverter) {
        return new RabbitEventProducer(rabbitTemplate, messageConverter);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.ons")
    public EventProducer onsEventPublisher(Producer producer,
                                           MessageConverter messageConverter) {
        return new AliOnsEventProducer(producer, messageConverter);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.kafka")
    public EventProducer kafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                             MessageConverter messageConverter) {
        return new KafkaEventProducer(kafkaTemplate, messageConverter);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.rocket")
    public EventProducer rocketEventPublisher(DefaultMQProducer defaultMQProducer,
                                              MessageConverter messageConverter) {
        return new RocketProducer(defaultMQProducer, messageConverter);
    }
}
