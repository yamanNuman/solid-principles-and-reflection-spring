package com.ymnn.payment.processor.impl;

import com.ymnn.payment.model.dto.PaymentDTO;
import com.ymnn.payment.model.response.PaymentResponse;
import com.ymnn.payment.processor.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaypalProcessor implements PaymentProcessor {
    @Override
    public String getPaymentMethod() {
        return "PAYPAL";
    }

    @Override
    public PaymentResponse process(PaymentDTO dto) {
        String txId = "PP-" + UUID.randomUUID();
        return PaymentResponse.success(txId, getPaymentMethod(), dto.getAmount(), dto.getCurrency());
    }
}
