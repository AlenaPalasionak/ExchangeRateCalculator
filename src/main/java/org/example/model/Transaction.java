package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.model.non_operating_income.AbstractNonOperatingIncome;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Transaction {
    private final List<Payment> incomingPaymentList;
    private final List<Payment> outgoingPaymentList;

    private final boolean accountBalance;
    private final String actDate;
    private final BigDecimal incomes;
    private final String actNumber;

    private final ExchangeRate actDateExchangeRate;

    private AbstractNonOperatingIncome nonOperatingIncome;
}
