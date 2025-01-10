package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.model.non_operating_income.AbstractExchangeIncome;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Payment {
    private BigDecimal paymentAmount;
    private String paymentDate;
    private ExchangeRate exchangeRate;
    private String currency;

    private AbstractExchangeIncome completionCertificateVSPaymentExchangeIncome;
    private AbstractExchangeIncome commissionExchangeIncome;
    private AbstractExchangeIncome receivedVSPaidExchangeIncome;
    private AbstractExchangeIncome accountExchangeIncome;

}
