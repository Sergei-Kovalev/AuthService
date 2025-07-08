package jdev.kovalev.service.impl;

import jdev.kovalev.entity.User;
import jdev.kovalev.exception.ConfirmationCodeNotValidException;
import jdev.kovalev.exception.EmailNotRegisteredException;
import jdev.kovalev.repository.UserRepository;
import jdev.kovalev.service.AuthService;
import jdev.kovalev.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String SENT_SUCCESSFULLY = "Verification code sent to %s successfully";

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public String createVerificationCode(String email) {
        String confirmationCode = generateConfirmationCode();

        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.setConfirmationCode(confirmationCode),
                        () -> userRepository.save(User.builder()
                                                          .email(email)
                                                          .confirmationCode(confirmationCode)
                                                          .build()));
        //TODO отправка по кафка
        System.out.println(confirmationCode);

        return String.format(SENT_SUCCESSFULLY, email);
    }

    @Override
    public String createAuthToken(String confirmationCode, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(EmailNotRegisteredException::new);
        if (!confirmationCode.equals(user.getConfirmationCode())) {
            throw new ConfirmationCodeNotValidException();
        }

        return jwtService.generateToken(email);
    }

    private String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(0, 1_000_000));
    }
}
