package com.academic.portal.service;

import com.academic.portal.entity.User;
import com.academic.portal.enums.Role;
import com.academic.portal.util.SessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RequestUserService {
    private final SessionManager sessionManager;

    public RequestUserService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public User requireUser(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Auth-Token header");
        }
        return sessionManager.getUser(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired session"));
    }

    public User requireRole(String token, Role role) {
        User user = requireUser(token);
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return user;
    }
}
