package com.markbolo.event.consumer;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 对@StreamListener注解的Consumer进行支持
 * 通过streamListener.name获取配置中对应的topic/tag等相关消费配置
 */
@ConfigurationProperties(prefix = ConsumerProperties.PREFIX)
public class ConsumerProperties {

    public static final String PREFIX = "spring.message.consumer";

    /**
     * key =  {@link StreamListener#name()}
     */
    Map<String, ConsumerConfiguration> consumers = new HashMap<>();

    @Data
    public static class ConsumerConfiguration {

        private String topic;

        /**
         * cid
         */
        private String consumerGroup;

        private Integer threadNum;

        private String tag;

        private Integer triggerNotificationTimes;

        // rabbitMq特异化配置
        private String exchange;

        private String routingKey;

        @Override
        public String toString() {
            return "ConsumerConfiguration{" +
                    "topic='" + topic + '\'' +
                    ", consumerGroup='" + consumerGroup + '\'' +
                    ", threadNum='" + threadNum + '\'' +
                    ", tag='" + tag + '\'' +
                    ", triggerNotificationTimes=" + triggerNotificationTimes +
                    '}';
        }


    }

    public Map<String, ConsumerConfiguration> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, ConsumerConfiguration> consumers) {
        this.consumers = consumers;
    }

    @Override
    public String toString() {
        return "ConsumerProperties{" +
                "consumers=" + consumers +
                '}';
    }
}
