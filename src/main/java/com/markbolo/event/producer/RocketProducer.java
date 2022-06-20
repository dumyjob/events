package com.markbolo.event.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

public class RocketProducer implements EventProducer {

    private final DefaultMQProducer defaultMQProducer;

    public RocketProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    @Override
    public void publish(String topic, String tag, String message) {
        /*
         * RocketMq支持多tag模式
         */
        try {
            byte[] body = message.getBytes(StandardCharsets.UTF_8);
            Message msg = new Message(topic, tag, body);
            defaultMQProducer.send(msg);
        } catch (MQClientException | RemotingException | MQBrokerException e) {
            throw new ProducerException("message produce exception:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            // 使其他线程能够感知此线程已经被中断
            Thread.currentThread().interrupt();
            throw new ProducerException(e);
        }
    }
}
