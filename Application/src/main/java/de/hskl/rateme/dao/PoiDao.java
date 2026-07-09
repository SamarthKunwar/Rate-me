package de.hskl.rateme.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.Poi;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class PoiDao {

    private final EntityManager entityManager;

    public PoiDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Poi> findAll() {
        return entityManager.createQuery("SELECT p FROM Poi p ORDER BY p.name", Poi.class).getResultList();
    }

    public Optional<Poi> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Poi.class, id));
    }
}
