package com.ymnn.payment.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDTO {

    private String paymentMethod;
    private double amount;
    private String currency;
    private String description;

}
