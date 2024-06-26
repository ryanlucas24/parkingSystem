package com.example.parkingsystem.domain.usecases.client;

import com.example.parkingsystem.domain.model.client.Client;
import com.example.parkingsystem.domain.model.payment.Payment;
import com.example.parkingsystem.domain.model.payment.PaymentMethodEnum;
import com.example.parkingsystem.domain.model.service.MonthlyService;
import com.example.parkingsystem.domain.model.service.StandardService;

public class MakePaymentUseCase {
    StandardService standardService;
    MonthlyService monthlyService;

    public Payment payment(Client client, PaymentMethodEnum paymentMethod) {
        standardService = new StandardService(0, client.getEntryDate());
        double value = standardService.calculateBilling(client.getEntryDate());
        return new Payment(value, paymentMethod);
    }

    public Payment monthlyPayment(Client client, PaymentMethodEnum paymentMethod) {
        monthlyService = new MonthlyService(false);
        double value = monthlyService.calculateBilling(client.getEntryDate());
        return new Payment(value, paymentMethod);
    }
}
