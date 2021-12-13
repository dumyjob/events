package com.markbolo.event;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

@Aspect
@Slf4j
public class CommitEventAspect {

    private final EventProducer eventProducer;

    public CommitEventAspect(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }


    @AfterReturning("@annotation(com.markbolo.event.CommitEvent)")
    public void commitEvent(JoinPoint joinPoint, Object ret) throws NoSuchMethodException {
        Class<?> clazz = joinPoint.getClass();
        String methodName = joinPoint.getSignature().getName();
        Method method = clazz.getMethod(methodName);

        if (!method.isAnnotationPresent(CommitEvent.class)) {
            return;
        }

        CommitEvent commitEvent = method.getAnnotation(CommitEvent.class);
        String topic = commitEvent.topic();
        String tag = commitEvent.tag();


        if ((StringUtils.isEmpty(topic)) && !ret.getClass().isAssignableFrom(Event.class)) {
            throw new IllegalArgumentException("required 'topic' must config.");
        }


        if (StringUtils.isNotEmpty(topic)) {
            eventProducer.produce(topic, tag, ret);
        } else {
            eventProducer.produce((Event) ret);
        }
    }
}
