package de.hskl.rateme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "poi")
public class Poi {

    @Id
    private Long id;

    @Column(nullable = false, length = 20)
    private String type;

    private Double lat;

    private Double lon;

    private String name;

    private String amenity;

    private String cuisine;

    private String phone;

    @Column(name = "opening_hours")
    private String openingHours;

    private String website;

    private String wheelchair;

    private String takeaway;

    private String delivery;

    private String smoking;

    @Column(name = "outdoor_seating")
    private String outdoorSeating;

    private String reservation;

    @Column(name = "addr_city")
    private String addrCity;

    @Column(name = "addr_country")
    private String addrCountry;

    @Column(name = "addr_housenumber")
    private String addrHousenumber;

    @Column(name = "addr_postcode")
    private String addrPostcode;

    @Column(name = "addr_street")
    private String addrStreet;

    @Column(nullable = false, columnDefinition = "json")
    private String tags;

    protected Poi() {
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getAmenity() {
        return amenity;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getPhone() {
        return phone;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getWebsite() {
        return website;
    }

    public String getWheelchair() {
        return wheelchair;
    }

    public String getTakeaway() {
        return takeaway;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getSmoking() {
        return smoking;
    }

    public String getOutdoorSeating() {
        return outdoorSeating;
    }

    public String getReservation() {
        return reservation;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public String getAddrCountry() {
        return addrCountry;
    }

    public String getAddrHousenumber() {
        return addrHousenumber;
    }

    public String getAddrPostcode() {
        return addrPostcode;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public String getTags() {
        return tags;
    }
}
