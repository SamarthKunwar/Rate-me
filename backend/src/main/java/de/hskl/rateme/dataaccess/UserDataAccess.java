package de.hskl.rateme.dataaccess;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import de.hskl.rateme.entity.User;

@Service
@Transactional
public class UserDataAccess {

    private final EntityManager entityManager;

    public UserDataAccess(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User create(User user) {
        entityManager.persist(user);
        entityManager.flush();
        entityManager.refresh(user);
        return user;
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResultOrNull());
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    //public void delete(User user){ }
}