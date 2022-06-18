package com.markbolo.event.consumer.registry;

import com.markbolo.event.consumer.adpater.MessageConsumer;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Spring:
 *  beanName: consumerId
 *  consumerId: {@link com.markbolo.event.consumer.ConsumerProperties} name对应的consumerId
 */
public class ConsumerFactory implements ApplicationRunner, ApplicationContextAware {

    private ConfigurableApplicationContext applicationContext;

    private final List<ConsumerFinder> consumerFinders;

    public ConsumerFactory(List<ConsumerFinder> consumerFinders) {
        this.consumerFinders = consumerFinders;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 触发consumer订阅消费
        for (ConsumerFinder finder : consumerFinders) {
            List<MessageConsumer> consumers = finder.findConsumers();
            if (CollectionUtils.isEmpty(consumers)) {
                continue;
            }

            // 是否可以用spring FactoryBean ?
            for (MessageConsumer consumer : consumers) {
                //
                applicationContext.getBeanFactory()
                        .registerSingleton(consumer.getClass() + "#" + consumer.name(), consumer);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
