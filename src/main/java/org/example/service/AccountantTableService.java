package org.example.service;

import org.example.repository.AccountantTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.constants.AccountantBookConstants.SHEET_1_B_M;

public class AccountantTableService {

    private final List<List<Object>> sheetDataTable;
    private static List<List<Object>> filteredTableByCellContentCache = null;

    public AccountantTableService(AccountantTable accountantBookRepository) {
        this.sheetDataTable = accountantBookRepository.getSheetDataTableCache(SHEET_1_B_M);
    }

    public List<List<Object>> getFilteredTableByCellContent(List<List<Object>> filterConditions) {
        if (filteredTableByCellContentCache == null) {
            filteredTableByCellContentCache = filterTableByCellContent(filterConditions);
        }
        return filteredTableByCellContentCache;
    }

    private List<List<Object>> filterTableByCellContent(List<List<Object>> filterConditions) {
        filteredTableByCellContentCache = sheetDataTable.stream()
                .filter(row -> {
                    // Для каждой строки проверяем все условия фильтрации
                    return filterConditions.stream().allMatch(condition -> {
                        int columnIndex = (int) condition.get(0); // Первый элемент — индекс столбца
                        String filterValue = (String) condition.get(1); // Второй элемент — значение или шаблон
                        if (columnIndex < 0 || columnIndex >= row.size()) {
                            // Если индекс столбца невалиден, исключаем строку
                            System.out.println("Invalid column index: " + columnIndex);
                            return false;
                        }
                        String cellValue = String.valueOf(row.get(columnIndex)); // Значение ячейки
                        // Сравниваем значение ячейки с шаблоном через регулярное выражение
                        return Pattern.compile(filterValue).matcher(cellValue).find();
                    });
                })
                .collect(Collectors.toCollection(ArrayList::new)); // Сохраняем результат в List
        return filteredTableByCellContentCache;
    }
}
