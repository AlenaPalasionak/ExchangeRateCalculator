package org.example.model.non_operating_income;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractExchangeIncome {
    protected BigDecimal incomeAmount;
    protected String journalEntry;
    private BigDecimal rate1;
    private BigDecimal rate2;
    protected BigDecimal amount;


}
