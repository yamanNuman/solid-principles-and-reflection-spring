package com.ymnn.payment.processor;

import com.ymnn.payment.model.dto.PaymentDTO;
import com.ymnn.payment.model.response.PaymentResponse;

public interface PaymentProcessor {
    String getPaymentMethod();
    PaymentResponse process(PaymentDTO dto);
}
