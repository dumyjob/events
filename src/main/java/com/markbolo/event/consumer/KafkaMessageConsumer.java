package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
public class KafkaMessageConsumer extends AbstractMessageConsumer{

    protected KafkaMessageConsumer(ConsumerProperties.ConsumerConfiguration configuration,
                                   MessageConverter messageConverter) {
        super(configuration, messageConverter);
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public String consumerId() {
        return null;
    }

    @Override
    public <T> MessageConsumer subscribe(Consumer<T> handler) {
        Properties props = new Properties();

        // 必须设置的属性
        props.put("bootstrap.servers", "192.168.239.131:9092");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", "group1");

        // 可选设置属性
        props.put("enable.auto.commit", "true");
        // 自动提交offset,每1s提交一次
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset","earliest ");
        props.put("client.id", "zy_client_id");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // 订阅 topic
        consumer.subscribe(Collections.singletonList(configuration.getTopic()));

        while(true) {
            //  从服务器开始拉取数据
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                log.info("topic = %s ,partition = %d,offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());

                // 如果不是需要的tag如何处理??
                Type type = handler.getClass().getGenericSuperclass();
                Type messageType = ((ParameterizedType) type).getActualTypeArguments()[0];
                Class<T> clazz = (Class<T>) messageType;

                T message = messageConverter.from(record.value(), clazz);
                handler.accept(message);
            });
        }

        // TODO while(true)如何处理??
        return this;
    }
}
