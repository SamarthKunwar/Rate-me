package de.hskl.rateme.dto;

public record LoginResponse(String sessionToken,
        CurrentUserDto user) {
}
