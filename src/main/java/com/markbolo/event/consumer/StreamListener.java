package com.markbolo.event.consumer;

import java.lang.annotation.*;

/**
 * <p> 提供注解方式消费消息
 * <pre>{@code
 * @StreamListener
 * public void consumer(X message){
 *     // 消息消费逻辑,如果没有抛出异常,ACK; 否则NO_ACK
 * }
 * }</pre>
 *
 * 需要配合{@link ConsumerProperties}配置使用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamListener {

    String name();

    /**
     * 支持多tag: tag1 || tag2
     */
    String tag();

    /**
     * consumer默认线程数
     */
    int threadNumber() default  1;

    /**
     * 触发消息消费失败通知次数
     */
    int triggerNotificationTimes() default 12;
}

