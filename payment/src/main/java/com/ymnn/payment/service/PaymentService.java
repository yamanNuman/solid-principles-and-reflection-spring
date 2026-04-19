package com.ymnn.payment.service;

import com.ymnn.payment.model.dto.PaymentDTO;
import com.ymnn.payment.model.request.PaymentRequest;
import com.ymnn.payment.model.response.PaymentResponse;
import com.ymnn.payment.processor.PaymentProcessor;
import com.ymnn.payment.processor.registry.PaymentProcessorRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PaymentService {

    private final PaymentProcessorRegistry registry;

    public PaymentService(PaymentProcessorRegistry registry) {
        this.registry = registry;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentDTO dto = PaymentDTO.builder()
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .build();

        PaymentProcessor processor = registry.getProcessor(request.getPaymentMethod());
        return processor.process(dto);
    }

    public Set<String> getSupportedPaymentMethods() {
        return registry.getSupportedPaymentMethods();
    }
}
