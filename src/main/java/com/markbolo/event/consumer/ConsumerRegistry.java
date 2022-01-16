package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.StreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerRegistry implements BeanPostProcessor, ApplicationRunner, ApplicationContextAware {

    private static final ConcurrentHashMap<Method, Object> consumers = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRegistry.class);

    private ConfigurableApplicationContext applicationContext;

    private final ConsumerProperties consumerProperties;

    private final List<MessageConsumerFactory> messageConsumerFactories;


    public ConsumerRegistry(ConsumerProperties consumerProperties,
                            List<MessageConsumerFactory> messageConsumerFactories) {
        this.consumerProperties = consumerProperties;
        this.messageConsumerFactories = messageConsumerFactories;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 找到@StreamListner注册的consumer
        Class<?> userType = ClassUtils.getUserClass(bean.getClass());
        Set<Method> streamListenerMethods = MethodIntrospector.selectMethods(userType, (ReflectionUtils.MethodFilter) method ->
                AnnotationUtils.findAnnotation(method, StreamListener.class) != null
        );
        if (!CollectionUtils.isEmpty(streamListenerMethods)) {
            streamListenerMethods.forEach(method -> consumers.putIfAbsent(method, bean));
        }
        return bean;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // start registry consumer into spring container and start consumer(by default)
        long startTime = System.currentTimeMillis();
        try {
            LOGGER.info("start message listener....");
            registry();
        } catch (Exception ex) {
            LOGGER.error("consumer failed to start ,exception:{},spring application context closed", ex.getMessage(), ex);
            applicationContext.close();
        }
        LOGGER.info("success to start message listener ,cost:{}", System.currentTimeMillis() - startTime);
    }


    private void registry() {
        if (CollectionUtils.isEmpty(consumers)) {
            return;
        }

        consumers.entrySet().forEach((methodObjectEntry -> {
            // @StreamListener method
            Method streamMethod = methodObjectEntry.getKey();
            Object bean = methodObjectEntry.getValue();

            // @StreamListener configuration key
            StreamListener annotation = AnnotationUtils.getAnnotation(streamMethod, StreamListener.class);
            if (annotation == null) {
                return;
            }
            String key = annotation.name();
            ConsumerProperties.ConsumerConfiguration consumerConfiguration = consumerProperties.getConsumers().get(key);

            // 是否可以用spring FactoryBean ?
            for (MessageConsumerFactory factory : messageConsumerFactories) {
                MessageConsumer messageConsumerBean = factory.createConsumer(consumerConfiguration,
                        message -> ReflectionUtils.invokeMethod(streamMethod, bean, message));

                applicationContext.getBeanFactory()
                        .registerSingleton(factory.getClass() + "#" + messageConsumerBean.consumerId(), messageConsumerBean);
            }
        }));

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
