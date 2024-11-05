package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Transaction {
    List<Payment> incomingPaymentList;
    List<Payment> outgoingPaymentList;

    private boolean accountBalance;
    private String actDate;
    private BigDecimal incomes;
    private String actNumber;

    private ExchangeRate actDateExchangeRate;
}
