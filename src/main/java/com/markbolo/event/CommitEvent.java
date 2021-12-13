package com.markbolo.event;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommitEvent {

    /**
     * 事件提交topic
     */
    String topic() default "";

    /**
     * 事件提交tag
     */
    String tag() default "";
}
