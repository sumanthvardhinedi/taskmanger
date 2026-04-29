package dev.sumanth.taskmanager.service;

import dev.sumanth.taskmanager.dao.UserRepository;
import dev.sumanth.taskmanager.dto.LoginRequest;
import dev.sumanth.taskmanager.dto.SignupRequest;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser signup(SignupRequest request) {
        String email = request.email().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        return users.save(new AppUser(request.name().trim(), email, passwordEncoder.encode(request.password())));
    }

    public AppUser login(LoginRequest request) {
        AppUser user = users.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return user;
    }
}
