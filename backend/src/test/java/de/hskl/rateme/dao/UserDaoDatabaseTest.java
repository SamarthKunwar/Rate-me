package de.hskl.rateme.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.hskl.rateme.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserDaoDatabaseTest {

    private final UserDao userDao;

    @Autowired
    UserDaoDatabaseTest(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    void userCanBeCreatedFoundAndDeleted() {
        User createdUser = userDao.create(createTestUser());

        assertNotNull(createdUser.getId());
        assertTrue(userDao.existsByUsername(createdUser.getUsername()));

        User foundUser = userDao.findByUsername(createdUser.getUsername()).orElseThrow();

        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(createdUser.getEmail(), foundUser.getEmail());

        userDao.delete(foundUser);

        assertFalse(userDao.findById(createdUser.getId()).isPresent());
    }

    private User createTestUser() {
        String username = "ud" + UUID.randomUUID().toString().substring(0, 8);

        return new User(
                username,
                username + "@example.com",
                "Test",
                "User",
                "Teststrasse",
                "1",
                "66482",
                "Zweibruecken",
                new byte[] { 1, 2, 3 },
                new byte[] { 4, 5, 6 });
    }
}
