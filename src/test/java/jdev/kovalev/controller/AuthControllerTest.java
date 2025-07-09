package jdev.kovalev.controller;

import jdev.kovalev.config.JwtAuthFilter;
import jdev.kovalev.service.AuthService;
import jdev.kovalev.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void createVerificationCode_ValidEmail_ReturnsOk() throws Exception {
        String email = "test@example.com";
        String responseMessage = "Code sent";

        when(authService.createVerificationCode(email)).thenReturn(responseMessage);

        mockMvc.perform(post("/auth/register")
                                .param("email", email)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string(responseMessage));

        verify(authService).createVerificationCode(email);
    }

    @Test
    void createVerificationCode_InvalidEmail_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                                .param("email", "invalid-email")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").value("createVerificationCode.email: Wrong Email format"),
                        jsonPath("$.path").value("/auth/register"),
                        jsonPath("$.status").value(400));
    }

    @Test
    void createVerificationCode_EmptyEmail_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                                .param("email", "")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").value("createVerificationCode.email: Email не может быть пустым"),
                        jsonPath("$.path").value("/auth/register"),
                        jsonPath("$.status").value(400));
    }

    @Test
    void createToken_ValidParams_ReturnsOk() throws Exception {
        String email = "test@example.com";
        String code = "123456";
        String token = "jwt-token";

        when(authService.createAuthToken(code, email)).thenReturn(token);

        mockMvc.perform(post("/auth/confirm")
                                .param("confirmationCode", code)
                                .param("email", email)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpectAll(
                        status().isOk(),
                        content().string(token));

        verify(authService).createAuthToken(code, email);
    }

    @Test
    void createToken_MissingParams_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/confirm")
                                // пропускаем параметры
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}