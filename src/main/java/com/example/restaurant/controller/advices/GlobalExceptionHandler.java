package com.example.restaurant.controller.advices;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HashMap<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        exception.printStackTrace();
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Database conflict: " + exception.getRootCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<HashMap<String, String>> handleDataAccessError(DataAccessException exception) {
        exception.printStackTrace();
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Database Error Occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<HashMap<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        exception.printStackTrace();
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Bad Input.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, String>> handleGeneralException(Exception exception) {
        exception.printStackTrace();
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Internal Server Error.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
