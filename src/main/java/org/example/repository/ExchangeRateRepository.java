package org.example.repository;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRateRepository {
    public Map<String, BigDecimal> getRateMap();
    //Repository Pattern: Используется для абстракции доступа к данным.
    // Каждый репозиторий отвечает за чтение или запись данных из/в определенный источник
    // (Google Sheets, Excel).

    // Интерфейс для работы с курсами обмена (чтение данных).
    //Метод readExchangeRates() будет возвращать список курсов из Excel.
}
