package de.hskl.rateme.dto;

public record CurrentUserDto(
        Integer id,
        String username,
        String email,
        String firstname,
        String lastname) {

}
