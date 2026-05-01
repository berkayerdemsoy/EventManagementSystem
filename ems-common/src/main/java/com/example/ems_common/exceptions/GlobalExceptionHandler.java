package com.example.ems_common.exceptions;

import com.example.ems_common.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException ex,
                                                          HttpServletRequest request) {

        ErrorResponseDto error = new ErrorResponseDto(
                "NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistsException(AlreadyExistsException ex,
                                                              HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "ALREADY_EXISTS",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex,
                                                          HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler({InvalidCredentialsException.class})
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentialsException(RuntimeException ex,
                                                                              HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "INVALID_CREDENTIALS",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ErrorResponseDto> handleForbiddenException(RuntimeException ex,
                                                              HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "FORBIDDEN",
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler({CannotJoinOwnEventException.class})
    public ResponseEntity<ErrorResponseDto> handleCannotJoinOwnEventException(CannotJoinOwnEventException ex,
                                                                              HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "CANNOT_JOIN_OWN_EVENT",
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }
}


