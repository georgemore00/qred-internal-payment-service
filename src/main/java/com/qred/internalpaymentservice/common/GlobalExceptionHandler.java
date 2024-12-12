package com.qred.internalpaymentservice.common;

import com.qred.internalpaymentservice.contract.exception.ContractNotFoundException;
import com.qred.internalpaymentservice.parsing.exception.FileParsingException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * A global exception handler that intercepts exceptions via Controller Advice AOP. Centralizes
 * error handling of exceptions and their appropriate HTTP responses.
 *
 * @author georgemore on 2024-12-10
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return ResponseEntity.badRequest().body(errors);
  }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations()
                .forEach(
                        violation -> {
                            String fieldName = violation.getPropertyPath().toString();
                            String errorMessage = violation.getMessage();
                            errors.put(fieldName, errorMessage);
                        });

        return ResponseEntity.badRequest().body(errors);
    }

  @ExceptionHandler(FileParsingException.class)
  public ResponseEntity<Map<String, String>> handleFileParsingException(FileParsingException ex) {
    Map<String, String> errors = new HashMap<>();
    errors.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleContractNotFoundException(ContractNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
