package org.example.model;

import lombok.*;
import org.example.model.non_operating_income.AbstractNonOperatingIncome;

import java.math.BigDecimal;
import java.util.LinkedList;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Transaction {

    private BigDecimal outstandingAmount;
    private LinkedList<Payment> incomingPaymentList;
    private LinkedList<Payment> outgoingPaymentList;
    private boolean accountBalance;
    private String actDate;
    private BigDecimal commission;
    private String actNumber;
    private ExchangeRate actDateExchangeRate;

    private AbstractNonOperatingIncome fromBeingPayedIncome;
    private AbstractNonOperatingIncome commissionIncome;
    private AbstractNonOperatingIncome fromDoingPaymentIncome;
    private AbstractNonOperatingIncome accountIncome;

    public Transaction(BigDecimal outstandingAmount, LinkedList<Payment> incomingPaymentList
            , LinkedList<Payment> outgoingPaymentList, boolean accountBalance, String actDate
            , BigDecimal commission, String actNumber, ExchangeRate actDateExchangeRate) {
        this.outstandingAmount = outstandingAmount;
        this.incomingPaymentList = incomingPaymentList;
        this.outgoingPaymentList = outgoingPaymentList;
        this.accountBalance = accountBalance;
        this.actDate = actDate;
        this.commission = commission;
        this.actNumber = actNumber;
        this.actDateExchangeRate = actDateExchangeRate;
    }
}
