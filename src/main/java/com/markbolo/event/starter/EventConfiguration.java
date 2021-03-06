package com.markbolo.event.starter;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markbolo.event.EventPublisher;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.alions.AliOnsConsumerFactory;
import com.markbolo.event.consumer.adpater.alions.AliyunOnsProperties;
import com.markbolo.event.consumer.adpater.kafka.KafkaConsumerFactory;
import com.markbolo.event.consumer.adpater.rabbit.RabbitConsumerFactory;
import com.markbolo.event.consumer.adpater.rocket.RocketConsumerFactory;
import com.markbolo.event.consumer.adpater.rocket.RocketProperties;
import com.markbolo.event.converter.JacksonMessageConverter;
import com.markbolo.event.converter.MessageConverter;
import com.markbolo.event.producer.*;
import com.markbolo.event.scheduler.EventScheduler;
import com.markbolo.event.storage.DataBaseEventStorage;
import com.markbolo.event.storage.EventStorage;
import com.markbolo.event.storage.dao.EventMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.kafka.core.KafkaTemplate;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

@Configuration
@MapperScan(markerInterface = Mapper.class)
public class EventConfiguration {

    @Bean("eventSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));
        return bean.getObject();
    }

    @Bean
    public EventStorage eventStorage(EventProducer eventProducer,
                                     EventMapper eventMapper) {
        return new DataBaseEventStorage(eventMapper, eventProducer);
    }

    @Bean
    public EventPublisher eventPublisher(EventStorage eventStorage,
                                         MessageConverter messageConverter) {
        EventPublisher.createInstance(eventStorage, messageConverter);
        return EventPublisher.instance();
    }


    @Bean
    public EventScheduler eventScheduler(EventStorage eventStorage,
                                         EventProducer eventProducer) {
        return new EventScheduler(eventProducer, eventStorage);
    }


    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MessageConverter jacksonMessageConverter() {
        return new JacksonMessageConverter();
    }


    @Bean
    @ConditionalOnProperty("spring.event.publisher.rabbit")
    public EventProducer rabbitEventProducer(RabbitTemplate rabbitTemplate) {
        return new RabbitEventProducer(rabbitTemplate);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.ons")
    public EventProducer onsEventProducer(Producer producer) {
        return new AliOnsEventProducer(producer);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.kafka")
    public EventProducer kafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaEventProducer(kafkaTemplate);
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.rocket")
    public EventProducer rocketEventProducer(DefaultMQProducer defaultMQProducer) {
        return new RocketProducer(defaultMQProducer);
    }

    /**
     * TODO ????????????????????????consumer
     */
    @ConditionalOnClass(ONSFactory.class)
    public MessageConsumerFactory onsConsumerFactory(MessageConverter messageConverter,
                                                     AliyunOnsProperties aliyunOnsProperties) {
        return new AliOnsConsumerFactory(messageConverter, aliyunOnsProperties);
    }

    @Bean
    @ConditionalOnClass(ConnectionFactory.class)
    public MessageConsumerFactory rabbitConsumerFactory(MessageConverter messageConverter,
                                                        ConnectionFactory connectionFactory) {
        return new RabbitConsumerFactory(messageConverter, connectionFactory);
    }


    @Bean
    @ConditionalOnClass(DefaultMQProducer.class)
    @ConditionalOnMissingClass({"com.aliyun.openservices.ons.api.ONSFactory"})
    public MessageConsumerFactory rocketConsumerFactory(MessageConverter messageConverter,
                                                        RocketProperties rocketProperties) {
        return new RocketConsumerFactory(messageConverter, rocketProperties);
    }

    @Bean
    @ConditionalOnClass(KafkaConsumer.class)
    public MessageConsumerFactory kafkaConsumerFactory(MessageConverter messageConverter) {
        return new KafkaConsumerFactory(messageConverter);
    }
}
