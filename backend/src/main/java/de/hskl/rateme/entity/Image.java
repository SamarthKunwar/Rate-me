package de.hskl.rateme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(nullable = false)
    private byte[] img;

    protected Image() {
    }

    public Image(byte[] img) {
        this.img = img;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getImg() {
        return img;
    }
}
