package com.markbolo.event;

/**
 * 账户余额增加事件
 */
public class AccountAmountAdd implements DomainEvent {

    @Override
    public String topic() {
        return null;
    }

    @Override
    public String tag() {
        return null;
    }
}
