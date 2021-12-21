package com.markbolo.event;


import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * spring-boot Loading Time Weaving Aspect
 */
@Aspect
public class CommitEventAspect {

    @AfterReturning("@annotation(com.markbolo.event.CommitEvent)")
    public void commitEvent(JoinPoint joinPoint, Object ret) throws NoSuchMethodException {
        Class<?> clazz = joinPoint.getClass();
        String methodName = joinPoint.getSignature().getName();
        Method method = clazz.getMethod(methodName);

        if (!method.isAnnotationPresent(CommitEvent.class)) {
            return;
        }

        CommitEvent commitTo = method.getAnnotation(CommitEvent.class);
        String topic = commitTo.topic();
        String tag = commitTo.tag();

        if ((StringUtils.isEmpty(topic)) && !ret.getClass().isAssignableFrom(Event.class)) {
            throw new IllegalArgumentException("required 'topic' must config.");
        }

        EventProducer eventProducer = ContextAware.getApplicationContext()
                .getBean(EventProducer.class);
        if (StringUtils.isNotEmpty(topic)) {
            eventProducer.produce(topic, tag, ret);
        } else {
            eventProducer.produce((Event) ret);
        }
    }
}
