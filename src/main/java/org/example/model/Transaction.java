package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.model.non_operating_income.AbstractExchangeIncome;

import java.math.BigDecimal;
import java.util.LinkedList;
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Transaction {

    private BigDecimal receivableAmount;
    private BigDecimal payableAmount;
    private LinkedList<Payment> incomingPaymentList;
    private LinkedList<Payment> outgoingPaymentList;
    private boolean accountBalance;
    private String actDate;
    private BigDecimal commission;
    private String actNumber;
    private ExchangeRate actDateExchangeRate;

    private AbstractExchangeIncome actPaymentExchangeIncome;
    private AbstractExchangeIncome commissionExchangeIncome;
    private AbstractExchangeIncome accountExchangeIncome;
    private AbstractExchangeIncome receivedPaidExchangeIncome;
}
