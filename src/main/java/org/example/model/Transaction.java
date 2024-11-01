package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    Payment incomingPayment;
    Payment outgoingPayment;

    private boolean accountBalance;
    private String actDate;
    private BigDecimal incomes;
    private String actNumber;

    private ExchangeRate actDateExchangeRate;
}
