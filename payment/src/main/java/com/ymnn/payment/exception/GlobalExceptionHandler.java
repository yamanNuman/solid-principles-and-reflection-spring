package com.ymnn.payment.exception;

import com.ymnn.payment.model.response.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedPaymentMethodException.class)
    public ResponseEntity<PaymentResponse> handleUnsupported(UnsupportedPaymentMethodException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(PaymentResponse.failure("UNKNOWN", e.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentRequestException.class)
    public ResponseEntity<PaymentResponse> handleInvalid(InvalidPaymentRequestException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(PaymentResponse.failure("UNKNOWN", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}
