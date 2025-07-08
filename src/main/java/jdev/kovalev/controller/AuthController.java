package jdev.kovalev.controller;

import jakarta.validation.constraints.NotBlank;
import jdev.kovalev.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> createVerificationCode(@RequestParam
                                             @NotBlank(message = "Email не может быть пустым")
                                             String email) {
        return ResponseEntity.ok(authService.createVerificationCode(email));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> createToken(@RequestParam
                                         @NotBlank(message = "Confirmation code не может быть пустым")
                                         String confirmationCode,
                                         @RequestParam
                                         @NotBlank(message = "Email не может быть пустым")
                                         String email) {
        return ResponseEntity.ok(authService.createAuthToken(confirmationCode, email));
    }
}
