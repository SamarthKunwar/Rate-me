package de.hskl.rateme.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    
    public byte[] generateSalt() {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return salt;
    }

    public byte[] hashPassword(String password, byte[] salt) {
        try {
                PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException("Could not hash password", e);
        }
    }

    public boolean passwordMatches(String password, byte[] salt, byte[] expectedHash) {
        byte[] actualHash = hashPassword(password, salt);
        return Arrays.equals(actualHash, expectedHash);
    }
}