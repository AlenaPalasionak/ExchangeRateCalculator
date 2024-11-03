package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Transaction {
    Payment incomingPayment;
    Payment outgoingPayment;

    private boolean accountBalance;
    private String actDate;
    private BigDecimal incomes;
    private String actNumber;

    private ExchangeRate actDateExchangeRate;
}
