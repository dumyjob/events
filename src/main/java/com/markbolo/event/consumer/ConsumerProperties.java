package com.markbolo.event.consumer;


import com.markbolo.event.StreamListener;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiashuai.xie
 */
@ConfigurationProperties(prefix = ConsumerProperties.PREFIX)
public class ConsumerProperties {

    public static final String PREFIX = "spring.message.consumer";

    /**
     * key =  {@link StreamListener#name()}
     */
    Map<String, ConsumerConfiguration> consumers = new HashMap<>();

    // TODO 可能需要支持特异化定制配置
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
