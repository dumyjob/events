package com.markbolo.event;

public interface Event {

    // 这里也不应该将topic/tag概念绑定到Event
    // 有什么方式解耦,同时也能作为基础服务暴露到其他服务中
    String topic();

    String tag();

    default long delay(){
        return 0L;
    }

    default String trackerId(){
        return "";
    }

    default String eventType(){
        return this.getClass().getName();
    }

}
