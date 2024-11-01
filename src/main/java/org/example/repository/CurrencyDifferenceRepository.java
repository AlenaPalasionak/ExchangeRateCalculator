package org.example.repository;

public interface CurrencyDifferenceRepository {
    //Repository Pattern: Используется для абстракции доступа к данным.
    // Каждый репозиторий отвечает за чтение или запись данных из/в определенный источник
    // (Google Sheets, Excel).

    //Интерфейс для записи курсовой разницы  в Google Sheets.
    // Метод writeCurrencyDiff(currencyDiff: CurrencyDiff): void будет записывать результаты.

    //CurrencyDiff сохраняется в базе данных через CurrencyDiffRepository.
}
