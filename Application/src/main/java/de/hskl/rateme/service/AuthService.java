package de.hskl.rateme.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import de.hskl.rateme.auth.PasswordService;
import de.hskl.rateme.dao.RatingDao;
import de.hskl.rateme.dao.UserDao;
import de.hskl.rateme.dto.LoginDtoOut;
import de.hskl.rateme.dto.UserDtoIn;
import de.hskl.rateme.dto.UserDtoOut;
import de.hskl.rateme.entity.Image;
import de.hskl.rateme.entity.Rating;
import de.hskl.rateme.entity.User;
import de.hskl.rateme.service.ImageService;
import de.hskl.rateme.auth.AuthTokenManager;
import de.hskl.rateme.dto.LoginDtoIn;

@Service
public class AuthService {

    private final UserDao userDao;
    private final RatingDao ratingDao;
    private final ImageService imageService;
    private final PasswordService passwordService;
    private final AuthTokenManager authTokenManager;

    public AuthService(UserDao userDao, RatingDao ratingDao, ImageService imageService,
            PasswordService passwordService, AuthTokenManager authTokenManager) {
        this.userDao = userDao;
        this.ratingDao = ratingDao;
        this.imageService = imageService;
        this.passwordService = passwordService;
        this.authTokenManager = authTokenManager;
    }

    public LoginDtoOut registerUser(UserDtoIn request) {

        if (userDao.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        byte[] salt = passwordService.generateSalt();
        byte[] hashedPassword = passwordService.hashPassword(request.password(), salt);

        User user = new User(request.username(), request.email(), request.firstname(), request.lastname(),
                request.street(), request.streetNr(), request.zip(), request.city(), hashedPassword, salt);
        userDao.create(user);

        String token = authTokenManager.createToken(user);

        UserDtoOut currentUser = new UserDtoOut(user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstname(), user.getLastname());
        return new LoginDtoOut(token, currentUser);
    }

    public LoginDtoOut loginUser(LoginDtoIn request) {
        Optional<User> userOptional = userDao.findByUsername(request.username());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        User user = userOptional.get();

        boolean passwordMatch = passwordService.passwordMatches(request.password(), user.getPasswordSalt(),
                user.getPasswordHash());
        if (!passwordMatch) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = authTokenManager.createToken(user);

        UserDtoOut currentUser = new UserDtoOut(user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstname(), user.getLastname());
        return new LoginDtoOut(token, currentUser);
    }

    public void logoutUser(String token) {
        authTokenManager.requireValidToken(token);
        authTokenManager.removeToken(token);
    }

    @Transactional
    public void deleteCurrentUser(String token) {
        User user = authTokenManager.getUser(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

        List<Rating> ratings = ratingDao.findByUserId(user.getId());
        for (Rating rating : ratings) {
            Image image = rating.getImage();
            ratingDao.delete(rating);
            imageService.deleteImage(image);
        }

        userDao.delete(user);
        authTokenManager.removeTokensForUser(user.getId());
    }
}
