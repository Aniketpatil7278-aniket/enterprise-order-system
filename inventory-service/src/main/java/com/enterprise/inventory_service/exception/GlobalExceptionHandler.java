package com.enterprise.inventory_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(
            InventoryNotFoundException.class
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            InventoryNotFoundException ex
    ) {

        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                404
        );

        response.put(
                "error",
                "Inventory Not Found"
        );

        response.put(
                "message",
                ex.getMessage()
        );


        return response;
    }


    @ExceptionHandler(
            InsufficientStockException.class
    )
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleStockError(
            InsufficientStockException ex
    ) {

        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                409
        );

        response.put(
                "error",
                "Insufficient Stock"
        );

        response.put(
                "message",
                ex.getMessage()
        );


        return response;
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors =
                new HashMap<>();


        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );


        return errors;
    }
}
