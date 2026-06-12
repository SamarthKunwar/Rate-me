package de.hskl.rateme.dto;

public record LoginDtoOut(String sessionToken,
        UserDtoOut user) {
}
