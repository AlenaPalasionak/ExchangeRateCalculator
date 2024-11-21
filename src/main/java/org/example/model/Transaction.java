package org.example.model;

import lombok.*;
import org.example.model.non_operating_income.AbstractExchangeIncome;

import java.math.BigDecimal;
import java.util.LinkedList;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
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

    public Transaction(BigDecimal receivableAmount, BigDecimal payableAmount
            , LinkedList<Payment> incomingPaymentList, LinkedList<Payment> outgoingPaymentList
            , boolean accountBalance, String actDate, BigDecimal commission, String actNumber
            , ExchangeRate actDateExchangeRate) {
        this.receivableAmount = receivableAmount;
        this.payableAmount = payableAmount;
        this.incomingPaymentList = incomingPaymentList;
        this.outgoingPaymentList = outgoingPaymentList;
        this.accountBalance = accountBalance;
        this.actDate = actDate;
        this.commission = commission;
        this.actNumber = actNumber;
        this.actDateExchangeRate = actDateExchangeRate;
    }
}
