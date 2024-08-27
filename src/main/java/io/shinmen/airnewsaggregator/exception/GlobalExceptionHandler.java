package io.shinmen.airnewsaggregator.exception;

import static io.shinmen.airnewsaggregator.utility.Messages.MESSAGE_ACCESS_DENIED;
import static io.shinmen.airnewsaggregator.utility.Messages.MESSAGE_AUTHENTICATION_FAILED;
import static io.shinmen.airnewsaggregator.utility.Messages.MESSAGE_INVALID_USERNAME_PASSWORD;
import static io.shinmen.airnewsaggregator.utility.Messages.MESSAGE_UNEXPECTED_ERROR;
import static io.shinmen.airnewsaggregator.utility.Messages.MESSAGE_VALIDATION_ERROR;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.shinmen.airnewsaggregator.payload.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AirNewsException.class)
    public ResponseEntity<ErrorResponse> handleAirNewsExceptions(final AirNewsException ex) {

        log.error("Exception occurred: {}", ex.getMessage(), ex);

        HttpStatus status = extractResponseStatusFromAnnotation(ex);

        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }

        final String message = ex.getMessage();

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message("ERROR: " + message)
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(NewsApiException.class)
    public ResponseEntity<ErrorResponse> handleNewsApiExceptions(final NewsApiException ex) {
        log.error("NewsApiException occurred: {}", ex.getMessage(), ex);

        final HttpStatus status = ex.getHttpStatus();

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message(ex.getCode().toUpperCase() + ": " + ex.getMessage())
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final List<String> errors = new ArrayList<>();

        errors.addAll(ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());

        errors.addAll(ex.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(MESSAGE_VALIDATION_ERROR)
                .timestamp(ZonedDateTime.now())
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException ex) {
        final List<String> errors = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(MESSAGE_VALIDATION_ERROR)
                .timestamp(ZonedDateTime.now())
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(final BadCredentialsException ex) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(MESSAGE_INVALID_USERNAME_PASSWORD)
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException ex) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(MESSAGE_ACCESS_DENIED)
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException ex) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(MESSAGE_AUTHENTICATION_FAILED)
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(final Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(MESSAGE_UNEXPECTED_ERROR)
                .timestamp(ZonedDateTime.now())
                .details(Arrays.asList(
                        ex.getMessage(),
                        ex.getClass().getCanonicalName()))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private HttpStatus extractResponseStatusFromAnnotation(final Exception ex) {
        final ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);

        if (responseStatus != null) {
            return responseStatus.value();
        }

        return null;
    }
}
