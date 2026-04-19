package com.ymnn.payment.controller;

import com.ymnn.payment.model.request.PaymentRequest;
import com.ymnn.payment.model.response.PaymentResponse;
import com.ymnn.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/payment")
@Tag(name= "Payment", description = "Payment Transactions")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Complete the payment process.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment successfully.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Unsupported payment method or validation error.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            )
    })
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @Operation(summary = "Supported payment methods list.")
    @ApiResponse(
            responseCode = "200",
            description = "Method list was successfully retrieved."
    )

    @GetMapping("/methods")
    public ResponseEntity<Set<String>> getSupportedPaymentMethods() {
        return ResponseEntity.ok(paymentService.getSupportedPaymentMethods());
    }

}
