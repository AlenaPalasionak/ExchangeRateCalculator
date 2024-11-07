package org.example.repository;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRateTable {

    public Map<String, BigDecimal> getExchangeRateTableCache();
}
