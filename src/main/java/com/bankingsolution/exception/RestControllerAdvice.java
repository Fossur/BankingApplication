package com.bankingsolution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(value = IllegalStateException.class)
    ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {

        var errorMessage = createErrorMessage(e, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalArgumentException e) {

        var errorMessage = createErrorMessage(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleJavaxException(MethodArgumentNotValidException e) {

        var errorMessage = createErrorMessage(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> createErrorMessage(MethodArgumentNotValidException e, HttpStatus httpStatus) {
        var errorResponse = new HashMap<String, String>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            var field = (FieldError) error;
            errorResponse.put(field.getField(), field.getDefaultMessage());
        });

        errorResponse.put("status", httpStatus.toString());
        return errorResponse;
    }


    private Map<String, String> createErrorMessage(Exception e, HttpStatus httpStatus) {
        var errorResponse = new HashMap<String, String>();
        errorResponse.put("message", e.getLocalizedMessage());
        errorResponse.put("status", httpStatus.toString());
        return errorResponse;
    }

}
