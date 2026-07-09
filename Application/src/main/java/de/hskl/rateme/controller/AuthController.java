package de.hskl.rateme.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import de.hskl.rateme.dto.LoginDtoIn;
import de.hskl.rateme.dto.LoginDtoOut;
import de.hskl.rateme.dto.UserDtoIn;
import de.hskl.rateme.service.AuthService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register, login, logout, and delete the current user")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginDtoOut> register(@RequestBody UserDtoIn request) {
        LoginDtoOut response = authService.registerUser(request); // HTTP status: 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Body: LoginDtoOut JSON
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginDtoOut> login(@RequestBody LoginDtoIn request) {
        LoginDtoOut response = authService.loginUser(request); // HTTP status: 200 OK
        return ResponseEntity.status(HttpStatus.OK).body(response); // Body: LoginDtoOut JSON
    }

    @Operation(summary = "Logout current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logoutUser(token);
        return ResponseEntity.noContent().build(); // HTTP status: 204 No Content
    }

    @Operation(summary = "Delete current user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("Authorization") String token) {
        authService.deleteCurrentUser(token); // HTTP status: 204 No Content
        return ResponseEntity.noContent().build(); // Body: empty
    }
}
