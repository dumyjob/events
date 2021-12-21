package com.markbolo.event.starter;

import com.markbolo.event.EventProducer;
import com.markbolo.event.publisher.*;
import com.markbolo.event.scheduler.EventScheduler;
import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.RowsEventStore;
import de.invesdwin.instrument.DynamicInstrumentationLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.transaction.TransactionManager;

@Configuration
@EnableLoadTimeWeaving
public class EventConfiguration {

    // 如何在应用启动的时候加载这个静态块
    static {
        //dynamically attach java agent to jvm if not already present
        DynamicInstrumentationLoader.waitForInitialized();
        //weave all classes before they are loaded as beans
        DynamicInstrumentationLoader.initLoadTimeWeavingContext();
    }

    @Bean
    public EventProducer eventProducer(EventStore eventStore,
                                       EventPublisher eventPublisher){
       return new EventProducer(eventStore, eventPublisher);
    }

    @Bean
    public EventScheduler  eventScheduler(EventStore eventStore,
                                          EventPublisher eventPublisher){
        return new EventScheduler(eventPublisher, eventStore);
    }

    @Bean
    public EventStore eventStore(TransactionManager transactionManager){
//        return new RowsEventStore();
        // 需要TransactionManager
        return null;
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.rabbit")
    public EventPublisher rabbitEventPublisher(){
        return new RabbitEventPublisher();
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.ons")
    public EventPublisher onsEventPublisher(){
        return new AliOnsEventPublisher();
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.kafka")
    public EventPublisher kafkaEventPublisher(){
        return new KafkaEventPublisher();
    }

    @Bean
    @ConditionalOnProperty("spring.event.publisher.rocket")
    public EventPublisher rocketEventPublisher(){
        return new RocketPublisher();
    }
}
