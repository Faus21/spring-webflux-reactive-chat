package com.example.reactivechat.handler;

import com.example.reactivechat.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class MyWebSocketHandler implements WebSocketHandler {

    private ObjectMapper objectMapper;

    private final Flux<Event> messages = Flux.<Event>create(sink -> {
        MyWebSocketHandler.this.sink = sink;
    }).share();

    private FluxSink<Event> sink;


    public MyWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::jsonToEvent)
                .doOnNext(message -> {
                    sink.next(message);
                })
                .subscribe();

        Flux<WebSocketMessage> messageFlux = messages.map(message -> session.textMessage(eventToJson(message)));
        return session.send(messageFlux);
    }

    private Event jsonToEvent(String json) {
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    private String eventToJson(Event event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
