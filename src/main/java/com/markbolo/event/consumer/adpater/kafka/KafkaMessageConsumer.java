package com.markbolo.event.consumer.adpater.kafka;

import com.markbolo.event.consumer.ConsumerException;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
public class KafkaMessageConsumer<T> extends AbstractMessageConsumer<T> {

    private KafkaConsumer<String, String> consumer;

    public KafkaMessageConsumer(ConsumerProperty consumerProperty,
        MessageConverter messageConverter,
        ConsumerHandler<T> handler) {
        super(consumerProperty, messageConverter, handler);
    }


    @Override
    public void start() {
        if (null == this.consumerProperty) {
            throw new ConsumerException("properties not set");
        }

        if (this.consumer != null && this.started.get()) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already started.", consumerProperty.getName());
            }
            return;
        }

        if (this.started.compareAndSet(false, true)) {
            // 避免并发影响
            subscribe();
        }
    }

    @Override
    public void close() {
        if (this.consumer == null) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already shutdown.", consumerProperty.getName());
            }
            return;
        }

        // 控制并发
        if (this.started.compareAndSet(true, false)) {
            consumer.close();
        }
    }

    @Override
    public boolean started() {
        return this.started.get();
    }

    @Override
    public boolean closed() {
        return !this.started.get();
    }

    @Override
    public void subscribe() {
        Properties props = new Properties();

        // 必须设置的属性 应该是关于链接的属性,和RabbitConnectionFactory以及RocketProperties一样,属于基础接入配置; 和每个consumer没有关系
        props.put("bootstrap.servers", "192.168.239.131:9092");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", "group1");

        // 可选设置属性
        props.put("enable.auto.commit", "true");
        // 自动提交offset,每1s提交一次
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest ");
        props.put("client.id", "zy_client_id"); // client_id和group.id的区别
        consumer = new KafkaConsumer<>(props);
        // 订阅 topic
        consumer.subscribe(Collections.singletonList(consumerProperty.getConfiguration().getTopic()));
        while (started()) {
            //  从服务器开始拉取数据
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(context -> {
                log.info("topic = %s ,partition = %d,offset = %d, key = %s, value = %s%n",
                    context.topic(), context.partition(), context.offset(), context.key(),
                    context.value());

                // 如果不是需要的tag如何处理??
                T message;
                try {
                    message = messageConverter.from(context.value(), handler.getGenericType());
                } catch (IOException e) {
                    throw new ConsumerException(e);
                }
                handler.handle(message);
            });

        }
    }
}
