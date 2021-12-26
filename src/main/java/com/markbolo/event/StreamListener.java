package com.markbolo.event;

/**
 * 如何注册不同client的Consumer??
 */
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

