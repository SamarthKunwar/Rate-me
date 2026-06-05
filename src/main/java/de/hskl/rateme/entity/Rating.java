package de.hskl.rateme.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    private Integer grade;

    @Column(name = "txt", nullable = false, length = 2000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Rating() {
    }

    public Rating(User user, Poi poi, Integer grade, String text, Image image) {
        this.user = user;
        this.poi = poi;
        this.grade = grade;
        this.text = text;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Poi getPoi() {
        return poi;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getText() {
        return text;
    }

    public Image getImage() {
        return image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void update(Integer grade, String text, Image image) {
        this.grade = grade;
        this.text = text;
        this.image = image;
    }
}
