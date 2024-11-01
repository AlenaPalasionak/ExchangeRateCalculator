package org.example.service;

import org.example.repository.AccountantBookRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.constants.AccountantBookConstant.SHEET_1_B_M;

public class AccountantBookService {
    AccountantBookRepository accountantBookRepository;
    private List<List<Object>> transactionCache;

    public AccountantBookService(AccountantBookRepository accountantBookRepository) {
        this.transactionCache = accountantBookRepository.getSheetDataTableCache(SHEET_1_B_M);
    }

    private List<List<Object>> filterTableByCellContent(int cellIndexName, String... cellContents) {
        transactionCache = transactionCache.stream()
                .filter(transaction -> {
                    String cellValue = String.valueOf(transaction.get(cellIndexName));
                    for (String content : cellContents) {
                        if (cellValue.contains(content)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

        return transactionCache;
    }

    private List<List<Object>> getTableInRusRubList(int paymentCellIndex, String currency) {
        return filterTableByCellContent(paymentCellIndex, currency);
    }
}
