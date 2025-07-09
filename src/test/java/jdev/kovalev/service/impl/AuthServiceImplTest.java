package jdev.kovalev.service.impl;

import jdev.kovalev.entity.User;
import jdev.kovalev.exception.ConfirmationCodeNotValidException;
import jdev.kovalev.exception.EmailNotRegisteredException;
import jdev.kovalev.repository.UserRepository;
import jdev.kovalev.service.JwtService;
import jdev.kovalev.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private AuthServiceImpl authService;

    private String testEmail;
    private String testCode;
    private static final String SENT_SUCCESSFULLY = "Confirmation code sent to %s successfully";
    private User testUser;

    @BeforeEach
    void setUp() {
        testEmail = "test@test.com";
        testCode = "123456";
        testUser = User.builder()
                .email(testEmail)
                .confirmationCode(testCode)
                .build();
    }

    @Nested
    class CreateVerificationCodeTests {
        @Test
        void createVerificationCode_ShouldSaveNewUser() {
            when(userRepository.findByEmail(testEmail))
                    .thenReturn(Optional.empty());

            String actual = authService.createVerificationCode(testEmail);

            verify(userRepository).save(argThat(user ->
                                                        user.getEmail().equals(testEmail) &&
                                                                user.getConfirmationCode() != null
                                               ));
            verify(kafkaProducerService).sendConfirmationCode(any());

            assertThat(actual)
                    .isEqualTo(String.format(SENT_SUCCESSFULLY, testEmail));
        }

        @Test
        void createVerificationCode_ShouldUpdateExistingUser() {
            when(userRepository.findByEmail(testEmail))
                    .thenReturn(Optional.of(testUser));

            authService.createVerificationCode(testEmail);

            verify(userRepository, never()).save(any());
            verify(userRepository).findByEmail(testEmail);
            assertThat(testUser.getConfirmationCode()).isNotEqualTo(testCode);
        }
    }

    @Nested
    class CreateAuthTokenTests {
        @Test
        void createAuthToken_ShouldReturnTokenForValidCode() {
            when(userRepository.findByEmail(testEmail))
                    .thenReturn(Optional.of(testUser));
            when(jwtService.generateToken(testEmail))
                    .thenReturn("new token");

            String actual = authService.createAuthToken(testCode, testEmail);

            assertThat(actual)
                    .isEqualTo("new token");
            verify(jwtService)
                    .generateToken(testEmail);
        }

        @Test
        void createAuthToken_ShouldThrowForInvalidCode() {
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> authService.createAuthToken("wrong code", testEmail))
                    .isInstanceOf(ConfirmationCodeNotValidException.class)
                    .hasMessageContaining("The confirmation code is invalid.");
        }

        @Test
        void createAuthToken_ShouldThrowForUnregisteredEmail() {
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.createAuthToken(testCode, testEmail))
                    .isInstanceOf(EmailNotRegisteredException.class)
                    .hasMessageContaining("Email not registered");
        }
    }
}