package bv.group.exchangerates.controllers;

import bv.group.exchangerates.exception.ExchangeNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(ExchangeNotFoundException.class)
    public ResponseEntity<String> exchangeRateErrorHandler(ExchangeNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An error occurred!" + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleGenericException(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>("Invalid currency: " + ex.getValue(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> validationErrorHandler(ConstraintViolationException ex){
        List<String> errorsList = new ArrayList<>(ex.getConstraintViolations().size());
        ex.getConstraintViolations().forEach(error -> errorsList.add(error.getMessage()));
        return new ResponseEntity<>(errorsList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
