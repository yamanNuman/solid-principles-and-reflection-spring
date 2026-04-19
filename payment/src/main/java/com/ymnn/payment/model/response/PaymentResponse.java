package com.ymnn.payment.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    @Schema(description = "Was the transaction successful?", example = "true")
    private boolean success;
    @Schema(description = "Transaction ID", example = "CC-123e4567-e89b")
    private String transactionId;
    @Schema(description = "Payment method", example = "PAYPAL")
    private String paymentMethod;
    @Schema(description = "Amount", example = "150.00")
    private double amount;
    @Schema(description = "Currency", example = "TRY")
    private String currency;
    @Schema(description = "Message", example = "Payment successfully completed.")
    private String message;
    @Schema(description = "Process time")
    private LocalDateTime processedAt;

    public static PaymentResponse success(String txId, String method, double amount, String currency) {
        return PaymentResponse.builder()
                .success(true)
                .transactionId(txId)
                .paymentMethod(method)
                .amount(amount)
                .currency(currency)
                .message("Payment successfully completed.")
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentResponse failure(String method, String message) {
        return PaymentResponse.builder()
                .success(false)
                .paymentMethod(method)
                .message(message)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
