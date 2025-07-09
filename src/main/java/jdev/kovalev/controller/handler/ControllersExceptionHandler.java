package jdev.kovalev.controller.handler;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import jdev.kovalev.exception.ConfirmationCodeNotValidException;
import jdev.kovalev.exception.EmailNotRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllersExceptionHandler {

    @ExceptionHandler(EmailNotRegisteredException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFoundException(Exception e, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            ConfirmationCodeNotValidException.class,
            JwtException.class
    })
    public ResponseEntity<CustomErrorResponse> handleConfirmationCodeNotValidException(Exception e, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; ", "", "."));

        CustomErrorResponse errorResponse = new CustomErrorResponse(message, HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(Exception e, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
