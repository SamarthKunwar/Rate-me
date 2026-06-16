package de.hskl.rateme.dao;

import java.util.Optional;
import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.Image;

@Service
@Transactional
public class ImageDao {

    private final EntityManager entityManager;

    public ImageDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Image create(Image image) {
        entityManager.persist(image);
        entityManager.flush();
        entityManager.refresh(image);
        return image;
    }

    public Optional<Image> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Image.class, id));
    }

    public void delete(Image image) {
        Image managedImage = entityManager.contains(image) ? image : entityManager.merge(image);
        entityManager.remove(managedImage);
        entityManager.flush();
    }

}
