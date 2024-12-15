package org.example.service;

import org.example.model.ExchangeRate;
import org.example.repository.ExchangeRateTable;
import org.example.repository.ExchangeRateTableImpl;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateTableService {
    private final Map<String, BigDecimal> exchangeRateTable;

    public ExchangeRateTableService() {
        ExchangeRateTable exchangeRateRepository = new ExchangeRateTableImpl();
        this.exchangeRateTable = exchangeRateRepository.getExchangeRateTableCache();
    }
    public ExchangeRate getExchangeRate(String paymentDate) {
        BigDecimal rate = exchangeRateTable.get(paymentDate);
        return new ExchangeRate(paymentDate, rate);
    }

    public BigDecimal getExchangeRateAmount(String paymentDate) {
        return exchangeRateTable.get(paymentDate);
    }

}
