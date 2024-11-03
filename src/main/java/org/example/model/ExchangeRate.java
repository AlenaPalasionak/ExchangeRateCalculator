package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class ExchangeRate {
    private String date;
    private BigDecimal rate;
    //String currency;
}
