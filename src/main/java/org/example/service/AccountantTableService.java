package org.example.service;

import org.example.repository.AccountantTable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.constants.AccountantBookConstant.SHEET_1_B_M;

public class AccountantTableService {

    private final List<List<Object>> sheetDataTable;
    private LinkedList<List<Object>> filteredTableByCellContentCache = null;

    public AccountantTableService(AccountantTable accountantBookRepository) {
        this.sheetDataTable = accountantBookRepository.getSheetDataTableCache(SHEET_1_B_M);
    }

    public LinkedList<List<Object>> getFilteredTableByCellContent(int cellIndexName, String... cellContents) {
        if (filteredTableByCellContentCache == null) {
            filteredTableByCellContentCache = filterTableByCellContent(cellIndexName, cellContents);
        }
        return filteredTableByCellContentCache;
    }

    private LinkedList<List<Object>> filterTableByCellContent(int cellIndexName, String... cellContents) {
        filteredTableByCellContentCache = sheetDataTable.stream()
                .filter(row -> {
                    String cellValue = String.valueOf(row.get(cellIndexName));
                    return Arrays.stream(cellContents).anyMatch(cellValue::contains);
                })
                .collect(Collectors.toCollection(LinkedList::new));
        return filteredTableByCellContentCache;
    }
}
