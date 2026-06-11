package de.hskl.rateme.auth;

import de.hskl.rateme.entity.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthTokenManager {

    private final Map<String, User> activeTokens = new ConcurrentHashMap<>();

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, user);
        return token;
    }

    public boolean removeToken(String token) {
        return activeTokens.remove(token) != null;
    }

    public boolean isValid(String token) {
        if (token == null) {
            return false;
        } else {
            return activeTokens.containsKey(token);
        }
    }

    public Optional<User> getUser(String token) {
        return Optional.ofNullable(activeTokens.get(token));
    }

    public void requireValidToken(String token) {
        if (!isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }
}
