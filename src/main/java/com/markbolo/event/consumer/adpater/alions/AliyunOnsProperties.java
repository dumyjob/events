package com.markbolo.event.consumer.adpater.alions;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ali-ons消费端配置
 */
@Data
@ConfigurationProperties(prefix = "ons")
public class AliyunOnsProperties {

    private String accessKey;

    private String secretKey;

    private String nameServerAddress;
}