package com.ymnn.payment.processor.registry;

import com.ymnn.payment.exception.UnsupportedPaymentMethodException;
import com.ymnn.payment.processor.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PaymentProcessorRegistry {

    private final Map<String, PaymentProcessor> paymentProcessorMap = new HashMap<>();
/*
Spring uygulama ayaga kalkarken
PaymentProcessor interface implemente eden tum @Component anotasyonla isaretlenmis beanleri bulur
ve bu beanleri bir liste halinde constructor inject eder.
 */
    public PaymentProcessorRegistry(List<PaymentProcessor> paymentProcessorList) {
        for (PaymentProcessor paymentProcessor : paymentProcessorList) {
            //Hangi interface implement ediyor => PaymentProcessor
            for(Class<?> iface: paymentProcessor.getClass().getInterfaces()) {
                if(iface.equals(PaymentProcessor.class)) {
                    paymentProcessorMap.put(
                            paymentProcessor.getPaymentMethod().toUpperCase(),
                            paymentProcessor
                    );
                    break;
                }
            }
        }
    }

    public PaymentProcessor getProcessor(String paymentMethod) {
        PaymentProcessor processor = paymentProcessorMap.get(paymentMethod.toUpperCase());
        if(processor == null) {
            throw new UnsupportedPaymentMethodException(
                    "Unsupported payment method: " + paymentMethod
            );
        }
        return processor;
    }

    public Set<String> getSupportedPaymentMethods() {
        return paymentProcessorMap.keySet();
    }
}
