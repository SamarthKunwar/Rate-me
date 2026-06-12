package de.hskl.rateme.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import de.hskl.rateme.dto.LoginDtoIn;
import de.hskl.rateme.dto.LoginDtoOut;
import de.hskl.rateme.dto.UserDtoIn;
import de.hskl.rateme.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public LoginDtoOut register(@RequestBody UserDtoIn request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginDtoOut login(@RequestBody LoginDtoIn request) {
        return authService.loginUser(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logoutUser(token);
    }

}
