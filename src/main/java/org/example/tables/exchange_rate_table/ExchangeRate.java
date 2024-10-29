package org.example.tables.exchange_rate_table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class ExchangeRate {
    private LocalDate date;
    private BigDecimal rate;
}
