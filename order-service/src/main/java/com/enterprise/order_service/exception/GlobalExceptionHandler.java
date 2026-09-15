package com.enterprise.order_service.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException exception){
        Map<String, Object> response=new HashMap<>();

        response.put("timestamp", LocalDateTime.now());

        response.put("status", 404);

        response.put("error", "ORDER_NOT_FOUND");

        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String ,Object>> handleBadRequest(IllegalArgumentException exception){
        Map<String ,Object> response=new HashMap<>();

        response.put("timestamp", LocalDateTime.now());

        response.put("status", 400);

        response.put("error", "BAD_REQUEST");

        response.put("message", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception){
        Map<String ,String> errors=new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error->errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        Map<String, Object> response= new HashMap<>();


        response.put("timestamp", LocalDateTime.now());

        response.put("status" , 400);

        response.put("error", "VALIDATION_ERROR");

        response.put("messages", errors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception exception){
        Map<String ,Object> response=new HashMap<>();

        response.put("timestamp", LocalDateTime.now());

        response.put("status", 500);

        response.put("error", "INTERNAL_SERVER_ERROR");

        response.put("message", "Something went wrong");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
