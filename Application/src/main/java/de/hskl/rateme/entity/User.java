package de.hskl.rateme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String firstname;

    @Column(nullable = false, length = 20)
    private String lastname;

    @Column(nullable = false, length = 30)
    private String street;

    @Column(name = "street_nr", nullable = false, length = 20)
    private String streetNr;

    @Column(nullable = false, length = 20)
    private String zip;

    @Column(nullable = false, length = 30)
    private String city;

    @Column(name = "password_hash", nullable = false)
    private byte[] passwordHash;

    @Column(name = "password_salt", nullable = false)
    private byte[] passwordSalt;

    protected User() {
    }

    public User(String username, String email, String firstname, String lastname,
            String street, String streetNr, String zip, String city,
            byte[] passwordHash, byte[] passwordSalt) {
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.street = street;
        this.streetNr = streetNr;
        this.zip = zip;
        this.city = city;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNr() {
        return streetNr;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }
}
