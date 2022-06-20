package com.markbolo.event;

import com.markbolo.event.converter.MessageConverter;
import com.markbolo.event.producer.ProducerException;
import com.markbolo.event.storage.Event;
import com.markbolo.event.storage.EventStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * USAGE:
 * <p>
 * EventProducer.createInstance()
 * <p>
 * EventProducer.instance()
 * .publish(final T domainEvent);
 */
@Slf4j
public class EventPublisher {

    private static EventPublisher instance;
    private final EventStorage eventStorage;
    private final MessageConverter messageConverter;

    public EventPublisher(EventStorage eventStorage,
                          MessageConverter messageConverter) {
        this.eventStorage = eventStorage;
        this.messageConverter = messageConverter;
    }

    public static void createInstance(EventStorage eventStorage,
                                      MessageConverter messageConverter) {
        instance = new EventPublisher(eventStorage, messageConverter);
    }

    public static EventPublisher instance() {
        Assert.notNull(instance, "event publisher not configured!!!");
        return instance;
    }

    public void produce(final String topic, final String tag, final Object message) {
        try {
            Event event = new Event(topic, tag, messageConverter.format(message), 0L);
            eventStorage.store(event);
        } catch (Exception e) {
            throw new ProducerException(e);
        }
    }

    public void produce(final String topic, final String tag, final Object message, long delay) {
        try {
            Event event = new Event(topic, tag, messageConverter.format(message), delay);
            eventStorage.store(event);
        } catch (Exception e) {
            throw new ProducerException(e);
        }
    }

    public void produce(final DomainEvent domainEvent) {
        try {
            Event event = new Event(domainEvent.topic(), domainEvent.tag(),
                    messageConverter.format(domainEvent), domainEvent.delay());
            eventStorage.store(event);
        } catch (Exception e) {
            // 异常是否能够用切面处理
            throw new ProducerException(e);
        }
    }
}
