package org.example.model.non_operating_income;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractExchangeIncome {
    public BigDecimal income;
    public String journalEntry;
}