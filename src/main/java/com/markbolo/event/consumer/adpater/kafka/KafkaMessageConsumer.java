package com.markbolo.event.consumer.adpater.kafka;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaMessageConsumer<T> extends AbstractMessageConsumer<T> {

    public KafkaMessageConsumer(ConsumerProperty consumerProperty,
                                MessageConverter messageConverter,
                                ConsumerHandler<T> handler) {
        super(consumerProperty, messageConverter, handler);
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
    public void subscribe() {
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
        props.put("auto.offset.reset", "earliest ");
        props.put("client.id", "zy_client_id");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // 订阅 topic
        consumer.subscribe(Collections.singletonList(consumerProperty.getConfiguration().getTopic()));


        while (true) {
            //  从服务器开始拉取数据
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                log.info("topic = %s ,partition = %d,offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());

                // 如果不是需要的tag如何处理??
                T message = null;
                try {
                    message = messageConverter.from(record.value(), handler.getGenericType());
                } catch (IOException e) {
                    // TODO 异常处理
                    e.printStackTrace();
                }
                handler.handle(message);
            });

        }
    }
}
