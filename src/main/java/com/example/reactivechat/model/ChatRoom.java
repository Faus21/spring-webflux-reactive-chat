package com.example.reactivechat.model;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

    private Map<String, String> users;

    public ChatRoom() {
        users = new HashMap<>();
    }

    public synchronized boolean addUser(String sessionId, String username){
        if (!users.containsKey(sessionId))
            if (!users.containsValue(username)){
                users.put(sessionId, username);
                return true;
            }
        return false;
    }

    public synchronized boolean removeUser(String sessionId){
        users.remove(sessionId);
        return true;
    }

    public boolean checkSessionId(String sessionId){
        if (users.containsKey(sessionId))
            return true;
        return false;
    }

    public boolean checkUsername(String sessionId, String username){
        if (users.containsValue(username) && users.get(sessionId).equals(username))
            return true;
        return false;
    }

    public Map<String, String> getUsers() {
        return users;
    }
}
