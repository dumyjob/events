package com.markbolo.event.consumer.registry;

import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.StreamListener;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link StreamListener}
 * 和spring beanPostProcessor怎么样?
 */
public class StreamListenerConsumerFinder implements ConsumerFinder, BeanPostProcessor {

    private static final ConcurrentHashMap<Method, Object> annotatedConsumers = new ConcurrentHashMap<>();

    private final ConsumerProperties consumerProperties;

    private final List<MessageConsumerFactory> messageConsumerFactories;

    public StreamListenerConsumerFinder(ConsumerProperties consumerProperties,
                                        List<MessageConsumerFactory> messageConsumerFactories) {
        this.consumerProperties = consumerProperties;
        this.messageConsumerFactories = messageConsumerFactories;
    }

    @Override
    public List<MessageConsumer> findConsumers() {
        if (CollectionUtils.isEmpty(annotatedConsumers)) {
            return Collections.emptyList();
        }

        List<MessageConsumer> consumers = new ArrayList<>();
        annotatedConsumers.entrySet().forEach((methodObjectEntry -> {
            // @StreamListener method
            Method streamMethod = methodObjectEntry.getKey();
            Object bean = methodObjectEntry.getValue();

            // @StreamListener configuration key
            StreamListener annotation = AnnotationUtils.getAnnotation(streamMethod, StreamListener.class);
            if (annotation == null) {
                return;
            }

            String consumerName = annotation.name();
            ConsumerProperties.ConsumerConfiguration consumerConfiguration = consumerProperties.getConsumers().get(consumerName);
            for (MessageConsumerFactory factory : messageConsumerFactories) {
                ConsumerProperty consumerProperty = new ConsumerProperty(consumerName, consumerConfiguration);
                ConsumerHandler<?> consumerHandler = new ConsumerHandler<>(message -> ReflectionUtils.invokeMethod(streamMethod, bean, message));
                MessageConsumer messageConsumerBean = factory.createConsumer(consumerProperty, consumerHandler);

                consumers.add(messageConsumerBean);
            }
        }));

        return consumers;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 找到@StreamListner注册的consumer method
        Class<?> userType = ClassUtils.getUserClass(bean.getClass());
        Set<Method> streamListenerMethods = MethodIntrospector.selectMethods(userType, (ReflectionUtils.MethodFilter) method ->
                AnnotationUtils.findAnnotation(method, StreamListener.class) != null
        );
        if (!CollectionUtils.isEmpty(streamListenerMethods)) {
            streamListenerMethods.forEach(method -> annotatedConsumers.putIfAbsent(method, bean));
        }
        return bean;
    }
}
