package com.academic.portal.util;

import com.academic.portal.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public Optional<User> getUser(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    public void removeSession(String token) {
        sessions.remove(token);
    }
}
