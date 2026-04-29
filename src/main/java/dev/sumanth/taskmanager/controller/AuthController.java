package dev.sumanth.taskmanager.controller;

import dev.sumanth.taskmanager.dto.LoginRequest;
import dev.sumanth.taskmanager.dto.SignupRequest;
import dev.sumanth.taskmanager.dto.UserResponse;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.security.CurrentUser;
import dev.sumanth.taskmanager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUser currentUser;

    public AuthController(AuthService authService, CurrentUser currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
    }

    @PostMapping("/signup")
    UserResponse signup(@Valid @RequestBody SignupRequest request, HttpServletRequest servletRequest) {
        AppUser user = authService.signup(request);
        loginSession(user, servletRequest);
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        AppUser user = authService.login(request);
        loginSession(user, servletRequest);
        return UserResponse.from(user);
    }

    @GetMapping("/me")
    UserResponse me() {
        return UserResponse.from(currentUser.require());
    }

    private void loginSession(AppUser user, HttpServletRequest servletRequest) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}

