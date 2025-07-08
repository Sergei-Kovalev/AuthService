package jdev.kovalev.service;

public interface AuthService {
    String createVerificationCode(String email);
    String createAuthToken(String confirmationCode, String email);
}
