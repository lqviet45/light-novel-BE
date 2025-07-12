package com.lqviet.userservice.exceptions;

import com.lqviet.userservice.dto.responses.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("USER_NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        log.warn("Email already exists: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("EMAIL_ALREADY_EXISTS")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        log.warn("Username already exists: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("USERNAME_ALREADY_EXISTS")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        log.warn("Invalid password: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("INVALID_PASSWORD")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Also handle global errors
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String objectName = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(objectName, errorMessage);
        });

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .errorCode("VALIDATION_ERROR")
                .fieldErrors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .errorCode("CONSTRAINT_VIOLATION")
                .fieldErrors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Data integrity violation";
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        // Try to provide more specific error messages
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email")) {
                message = "Email already exists";
                errorCode = "EMAIL_ALREADY_EXISTS";
            } else if (ex.getMessage().contains("username")) {
                message = "Username already exists";
                errorCode = "USERNAME_ALREADY_EXISTS";
            }
        }

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message("Malformed JSON request")
                .errorCode("MALFORMED_JSON")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode("INVALID_PARAMETER_TYPE")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage());

        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode("MISSING_PARAMETER")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());

        String message = String.format("Method '%s' is not supported for this endpoint", ex.getMethod());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode("METHOD_NOT_SUPPORTED")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler found: {}", ex.getMessage());

        String message = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode("ENDPOINT_NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("ILLEGAL_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .message("An unexpected error occurred")
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}