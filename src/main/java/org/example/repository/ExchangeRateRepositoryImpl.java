package org.example.repository;

import org.example.config.Config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository{
    private static final String excelFilePath = Config.getProperties().getProperty("rate_excel_file");
    Map<String, BigDecimal> dataMap = new HashMap<>();

    @Override
    public Map<String, BigDecimal> getExchangeRateMap() {
        dataMap = SingletonExchangeRate.getExchangeRateInstance().getMap();
        return dataMap;
    }


}
