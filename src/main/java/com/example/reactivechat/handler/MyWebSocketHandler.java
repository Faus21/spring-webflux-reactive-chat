package com.example.reactivechat.handler;

import com.example.reactivechat.model.ChatRoom;
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

    private ChatRoom chatRoom;

    private final Flux<Event> messages = Flux.<Event>create(sink -> {
        MyWebSocketHandler.this.sink = sink;
    }).share();

    private FluxSink<Event> sink;


    public MyWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.chatRoom = new ChatRoom();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::jsonToEvent)
                .doOnNext(message -> {
                    //message.setSender(session.getId());
                    if (!chatRoom.checkSessionId(session.getId()))
                        if (!chatRoom.addUser(session.getId(), message.getSender())){
                            session.send(Mono.just(session.textMessage("Error: User already exist")))
                                    .then()
                                    .subscribe();
                            return;
                        }
                    if (chatRoom.checkUsername(session.getId(), message.getSender())) {
                        sink.next(message);
                        return;
                    }

                    session.send(Mono.just(session.textMessage("Error: Use your previous username")))
                            .then()
                            .subscribe();
                })
                .doFinally(event -> chatRoom.removeUser(session.getId()))
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
