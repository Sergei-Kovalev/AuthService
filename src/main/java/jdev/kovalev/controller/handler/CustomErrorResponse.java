package jdev.kovalev.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Data
@Slf4j
@AllArgsConstructor
public class CustomErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String path;

    public CustomErrorResponse(String message, HttpStatus status, WebRequest request) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = status.value();
        this.error = message;
        this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.info("Принят ответ с ошибкой: отметка времени {} статус {} ошибка {} путь {}", timestamp, status, error, path);
    }
}
