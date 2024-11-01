package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Payment {
    private BigDecimal payment;
    private String paymentDate;
    private ExchangeRate exchangeRate;
    private String currency;
}
