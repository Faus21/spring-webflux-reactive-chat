package com.example.reactivechat.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Event {

    private static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Integer id;

    private String sender;

    private String message;

    private Long timestamp;

    public Event() {
        this.id = ID_GENERATOR.addAndGet(1);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
