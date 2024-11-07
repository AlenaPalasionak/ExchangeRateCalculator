package org.example.model.non_operating_income;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractNonOperatingIncome {
    private BigDecimal income;
    private String journalEntry;
}
