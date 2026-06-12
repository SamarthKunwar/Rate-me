package de.hskl.rateme.service;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.auth.PasswordService;
import de.hskl.rateme.dao.UserDao;
import de.hskl.rateme.dto.LoginDtoOut;
import de.hskl.rateme.dto.UserDtoIn;
import de.hskl.rateme.dto.UserDtoOut;
import de.hskl.rateme.entity.User;
import de.hskl.rateme.auth.AuthTokenManager;

@Service
public class AuthService {

    private final UserDao userDao;
    private final PasswordService passwordService;
    private final AuthTokenManager authTokenManager;

    public AuthService(UserDao userDao, PasswordService passwordService, AuthTokenManager authTokenManager) {
        this.userDao = userDao;
        this.passwordService = passwordService;
        this.authTokenManager = authTokenManager;
    }

    public LoginDtoOut registerUser(UserDtoIn request) {

        if (userDao.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
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

}
