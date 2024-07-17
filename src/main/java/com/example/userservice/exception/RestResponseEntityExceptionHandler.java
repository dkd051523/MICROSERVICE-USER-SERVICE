package com.example.userservice.exception;


import com.example.userservice.core.BaseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({CustomListValidationException.class})
    public ResponseEntity<Object> handleValidationException(
            CustomListValidationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(
                new BaseResponse<>(400, null, errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            CustomException ex, WebRequest request) {

        return new ResponseEntity<>(
                new BaseResponse<String>(ex.getStatusCode(), ex.getMessage(), null), new HttpHeaders(), HttpStatus.valueOf(ex.getStatusCode()));
    }
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object>  handleAccessDeniedException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new BaseResponse<String>(500, ex.getMessage(), null), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }


}