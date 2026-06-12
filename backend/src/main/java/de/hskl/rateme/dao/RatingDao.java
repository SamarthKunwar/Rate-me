package de.hskl.rateme.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.Rating;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class RatingDao {

    private final EntityManager entityManager;

    public RatingDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Rating create(Rating rating) {
        entityManager.persist(rating);
        entityManager.flush();
        entityManager.refresh(rating);
        return rating;
    }

    public Optional<Rating> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Rating.class, id));
    }

    public Optional<Rating> findByIdWithDetails(Integer id) {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM Rating r
                        JOIN FETCH r.user
                        JOIN FETCH r.poi
                        LEFT JOIN FETCH r.image
                        WHERE r.id = :id
                        """, Rating.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<Rating> findByPoiId(Long poiId) {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM Rating r
                        JOIN FETCH r.user
                        LEFT JOIN FETCH r.image
                        WHERE r.poi.id = :poiId
                        ORDER BY r.createdAt DESC
                        """, Rating.class)
                .setParameter("poiId", poiId)
                .getResultList();
    }

    public List<Rating> findByUserId(Integer userId) {
        return entityManager
                .createQuery("""
                        SELECT r
                        FROM Rating r
                        JOIN FETCH r.poi
                        LEFT JOIN FETCH r.image
                        WHERE r.user.id = :userId
                        ORDER BY r.createdAt DESC
                        """, Rating.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Rating update(Rating rating) {
        Rating updatedRating = entityManager.merge(rating);
        entityManager.flush();
        entityManager.refresh(updatedRating);
        return updatedRating;
    }

    public void delete(Rating rating) {
        Rating managedRating = entityManager.contains(rating) ? rating : entityManager.merge(rating);
        entityManager.remove(managedRating);
        entityManager.flush();
    }

    public int deleteByUserId(Integer userId) {
        return entityManager
                .createQuery("DELETE FROM Rating r WHERE r.user.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
