package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Payment result is required")
    private String paymentResult;

    public PaymentRequest() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            String paymentMethod) {

        this.paymentMethod = paymentMethod;
    }

    public String getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(
            String paymentResult) {

        this.paymentResult = paymentResult;
    }
}