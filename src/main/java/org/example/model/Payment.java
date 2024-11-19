package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
}
