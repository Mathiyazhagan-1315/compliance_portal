package com.academic.portal.service;

import com.academic.portal.dto.LoginRequest;
import com.academic.portal.dto.LoginResponse;
import com.academic.portal.entity.User;
import com.academic.portal.repository.UserRepository;
import com.academic.portal.util.SessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionManager = sessionManager;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = sessionManager.createSession(user);
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public void logout(String token) {
        sessionManager.removeSession(token);
    }
}
