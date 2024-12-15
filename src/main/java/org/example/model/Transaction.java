package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.model.non_operating_income.AbstractExchangeIncome;

import java.math.BigDecimal;
import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Transaction {

    private BigDecimal receivableAmount;
    private BigDecimal payableAmount;
    private List<Payment> incomingPaymentList;
    private List<Payment> outgoingPaymentList;
    private boolean accountBalance;
    private String actDate;
    private BigDecimal commission;
    private String actNumber;
    private ExchangeRate actDateExchangeRate;

    private AbstractExchangeIncome completionCertificateVSPaymentExchangeIncome;
    private AbstractExchangeIncome commissionExchangeIncome;
    private AbstractExchangeIncome receivedVSPaidExchangeIncome;
    private AbstractExchangeIncome accountExchangeIncome;
}
