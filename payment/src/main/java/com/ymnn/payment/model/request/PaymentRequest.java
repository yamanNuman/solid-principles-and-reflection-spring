package com.ymnn.payment.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @Schema(description = "Payment Method", example = "CREDIT_CARD")
    @NotBlank(message = "Payment method cannot be empty.")
    private String paymentMethod;

    @Schema(description = "Payment amount", example = "150.00")
    @Positive(message = "The amount must be greater than 0.")
    private double amount;

    @Schema(description = "Currency", example = "TRY")
    @NotBlank(message = "Currency cannot be empty.")
    private String currency;

    @Schema(description = "Description", example = "Test payment")
    private String description;
}
