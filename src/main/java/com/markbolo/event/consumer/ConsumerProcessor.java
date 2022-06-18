package com.markbolo.event.consumer;

/**
 * <p> 提供接口实现方式消费消息
 * <pre>{@code
 *     UserAmountAddConsumer extents ConsumerProcessor<UserAmountAdded> {
 *         void handle(UserAmountAdded message){
 *              // 消息消费逻辑, 无异常ACK;异常NO_ACK
 *         }
 *     }
 * }</pre>
 * <p>
 *
 * 和{@link StreamListener}类似,也是通过name找到消费相关的配置
 * 1. 是否要支持ConsumerContext,消息消费上下文
 */
public interface ConsumerProcessor<T> {

    /**
     * 消息消费逻辑实现
     */
    void handle(T message);

    /**
     * 消费者名称
     */
    String name();

    Class<?> getGenericType();
}
