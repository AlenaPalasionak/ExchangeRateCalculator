package org.example.service;

import org.example.repository.AccountantBookRepositoryImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.constants.AccountantBookConstant.SHEET_1_B_M;

public class AccountantBookService {

    private final List<List<Object>> transactionCache;
    private List<List<Object>> filteredTransactionCache = null;

    public AccountantBookService(AccountantBookRepositoryImpl accountantBookRepository) {
        this.transactionCache = accountantBookRepository.getSheetDataTableCache(SHEET_1_B_M);
    }

    public List<List<Object>> getFilteredTableByCellContent(int cellIndexName, String... cellContents) {
        if (filteredTransactionCache == null) {
            filteredTransactionCache = filterTableByCellContent(cellIndexName, cellContents);
        }
        return filteredTransactionCache;
    }

    private List<List<Object>> filterTableByCellContent(int cellIndexName, String... cellContents) {
        filteredTransactionCache = transactionCache.stream()
                .filter(row -> {
                    String cellValue = String.valueOf(row.get(cellIndexName));
                    return Arrays.stream(cellContents).anyMatch(cellValue::contains);
                })
                .collect(Collectors.toList());
        return filteredTransactionCache;
    }
}