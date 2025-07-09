package jdev.kovalev.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtServiceImplTest {

    @Autowired
    private JwtServiceImpl jwtService;

    private String email;

    @BeforeEach
    void setUp() {
        email = "test@test.com";
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(email);

        assertThat(token)
                .isNotBlank();
        assertThat(jwtService.extractEmail(token))
                .isEqualTo(email);
        assertThat(jwtService.isTokenValid(token))
                .isTrue();
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        String token = jwtService.generateToken(email);

        String actual = jwtService.extractEmail(token);

        assertThat(actual)
                .isEqualTo(email);
    }

    @Test
    void extractEmail_ShouldThrowOnInvalidToken() {
        assertThatThrownBy(() -> jwtService.extractEmail("invalidToken"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String validToken = jwtService.generateToken(email);

        assertThat(jwtService.isTokenValid(validToken))
                .isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidSignature() {
        String validToken = jwtService.generateToken(email);
        String brokenToken = validToken.substring(0, validToken.length() - 5) + "12345";

        assertThat(jwtService.isTokenValid(brokenToken))
                .isFalse();
    }
}