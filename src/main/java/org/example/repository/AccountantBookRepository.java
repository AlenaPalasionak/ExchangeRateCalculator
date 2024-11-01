package org.example.repository;

import java.util.List;

public interface AccountantBookRepository {
    public List<List<Object>> getSheetDataTableCache(String rage);


    //Repository Pattern: Используется для абстракции доступа к данным.
    // Каждый репозиторий отвечает за чтение или запись данных из/в определенный источник
    // (Google Sheets, Excel).

    //Интерфейс для работы с платежами (чтение данных).
    //readPayments() будет возвращать список платежей из Google Sheets.
}
