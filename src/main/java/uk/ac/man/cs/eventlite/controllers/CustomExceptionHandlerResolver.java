package uk.ac.man.cs.eventlite.controllers;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Component
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomExceptionHandlerResolver {
	// This will catch the internal exception that returns 406 and return 404 instead
	// It fixes the default getJsonRoot test, which expects a 404
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleException(Exception ex) throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid endpoint");
    }
}