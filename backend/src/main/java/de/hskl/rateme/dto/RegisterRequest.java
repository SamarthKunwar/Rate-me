package de.hskl.rateme.dto;

public record RegisterRequest(String username,
                String password,
                String email,
                String firstname,
                String lastname,
                String street,
                String streetNr,
                String zip,
                String city) {

}
