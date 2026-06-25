package com.featureflags.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.featureflags.controller.api")
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // 404 - flag not found by id or name.
    @ExceptionHandler(FlagNotFoundException.class)
    public ResponseEntity<Object> handleFlagNotFoundException(
            FlagNotFoundException exception, WebRequest request) {
        Map<String, Object> body = buildErrorBody(HttpStatus.NOT_FOUND, exception.getMessage(), request);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 409 - duplicate flag name on create or rename.
    @ExceptionHandler(DuplicateFlagNameException.class)
    public ResponseEntity<Object> handleDuplicateFlagNameException(
            DuplicateFlagNameException exception, WebRequest request) {
        Map<String, Object> body = buildErrorBody(HttpStatus.CONFLICT, exception.getMessage(), request);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 400 - JPA level constraint violation.
    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(
            Exception exception, WebRequest request) {
        Map<String, Object> body = buildErrorBody(HttpStatus.BAD_REQUEST, exception.getMessage(), request);

        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed.");

        Map<String, Object> body = buildErrorBody(HttpStatus.BAD_REQUEST, message, request);
        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // 500 - all unexpected errors.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception exception, WebRequest request) {
        Map<String, Object> body = buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred!", request);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> buildErrorBody(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }


}
