package com.example.parkingsystem.domain.usecases.parking;

import com.example.parkingsystem.domain.model.client.Client;
import com.example.parkingsystem.domain.model.parking.Parking;
import com.example.parkingsystem.domain.model.service.MonthlyService;
import com.example.parkingsystem.domain.model.service.StandardService;
import com.example.parkingsystem.domain.usecases.client.ClientBasicInformationInputRequestValidator;
import com.example.parkingsystem.domain.usecases.client.ClientDAO;
import com.example.parkingsystem.domain.usecases.utils.Notification;
import com.example.parkingsystem.domain.usecases.utils.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegisterExitUseCase {

    private ParkingDAO parkingDAO;
    private ClientDAO clientDAO;
    private Parking parking;
    private boolean paymentChecked;

    public RegisterExitUseCase(ParkingDAO parkingDAO, ClientDAO clientDAO) {
        this.parkingDAO = parkingDAO;
        this.clientDAO = clientDAO;
    }

    public Client getClientByCpf(String cpf){
        Optional<Client> clientOptional = clientDAO.findOne(cpf);
        if(clientOptional.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        Client clientFound = clientOptional.get();
        MonthlyService monthlyService = (MonthlyService) clientFound.getService();
        StandardService standardService = (StandardService) clientFound.getService();

        if (clientFound.getService() == null) {
            throw new IllegalArgumentException("Cliente não possui serviço ativo");
        }

        if (monthlyService != null && standardService != null) {
            throw new IllegalArgumentException("Cliente possui mais de um serviço ativo");
        }

        //fluxo principal

        if (clientFound.getService() == standardService) {
            if (standardService.getCheckOut() != null) {
                throw new IllegalArgumentException("Cliente já realizou o pagamento");
            }
            // Chama o cálculo de valor do pagamento para serviço standard
            standardService.setCheckOut(LocalDateTime.now());
            standardService.calculateBilling();
            setPaymentChecked(true);
            if (isPaymentChecked()) {
                // Libera a vaga e o cliente
                parking.liberateAStandardParkingSpace();
            } else {
                throw new IllegalArgumentException("Pagamento não efetuado");
            }

        }

        // Fluxo alternativo 1
        if (clientFound.getService() == monthlyService) {
            if (monthlyService.isPaymentChecked()) {
                // Libera a vaga e o cliente
                parking.liberateAMonthlyParkingSpace();
            } else {
                throw new IllegalArgumentException("Pagamento não efetuado");
            }
        }
        return clientFound;
    }

    public boolean isPaymentChecked() {
        return paymentChecked;
    }

    public void setPaymentChecked(boolean paymentChecked) {
        this.paymentChecked = paymentChecked;
    }

}
