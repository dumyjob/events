package com.markbolo.event.consumer.adpater.rocket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMa消费配置
 */
@Data
@ConfigurationProperties(prefix = "rocket.consumer")
public class RocketProperties {

    private String nameServer;
    private String instanceName;
}
