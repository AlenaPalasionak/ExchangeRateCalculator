package org.example.model.non_operating_income;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractExchangeIncome {
    protected BigDecimal exchangeIncomeAmount;
    protected String journalEntry;
    protected BigDecimal rate1;
    protected BigDecimal rate2;
    protected BigDecimal paymentAmount;
}
