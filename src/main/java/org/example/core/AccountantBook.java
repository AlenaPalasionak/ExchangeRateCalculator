package org.example.core;

import org.example.util.google_sheet_handler.GoogleSheetHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.core.constants.AccountantBookConstant.*;
import static org.example.util.google_sheet_handler.constants.KeyWordConstant.RUS_RUB;
import static org.example.util.google_sheet_handler.constants.SheetRangeConstant.SHEET_1_B_M;

public class AccountantBook {

    List<Transaction> transactionList;

    public List<Transaction> getTransactionsInRusRubList() {
        transactionList = new ArrayList<>();

        for (List<Object> transactionsObjectsList : getTransactionsObjectsInRusRubList()) {
            Transaction transaction = new Transaction();

            BigDecimal incomingPaymentSum = retrieveNum(INCOMING_PAYMENT_SUM, transactionsObjectsList);
            transaction.setIncomingPaymentSum(incomingPaymentSum);

            transaction.setIncomingPaymentDate(retrieveStr(INCOMING_PAYMENT_DATE, transactionsObjectsList));

            BigDecimal outgoingPaymentSum = retrieveNum(OUTGOING_PAYMENT_SUM, transactionsObjectsList);
            transaction.setOutgoingPaymentSum(retrieveNum(OUTGOING_PAYMENT_SUM, transactionsObjectsList));

            transaction.setOutgoingPaymentDate(retrieveStr(OUTGOING_PAYMENT_DATE, transactionsObjectsList));

            transaction.setIncomes(countIncomes(incomingPaymentSum, outgoingPaymentSum));

            transaction.setActDate(retrieveStr(ACT_DATE, transactionsObjectsList));

            transaction.setAccountBalance(isBalance(transactionsObjectsList));

            transaction.setActNumber(retrieveStr(ACT_NUMBER, transactionsObjectsList));

            transactionList.add(transaction);
        }
        return transactionList;
    }

    private List<List<Object>> getTransactionsObjectsInRusRubList() {
        return GoogleSheetHandler.filterTableByCellContent(SHEET_1_B_M, INCOMING_PAYMENT_SUM, RUS_RUB);
    }

    private BigDecimal retrieveNum(int index, List<Object> objects) {
        return new BigDecimal(retrieveStr(index, objects).replaceAll("[а-я, А-Я]", ""));
    }

    private String retrieveStr(int index, List<Object> objects) {
        return objects.get(index).toString().trim();
    }

    private BigDecimal countIncomes(BigDecimal income, BigDecimal outgoings) {
        return income.subtract(outgoings);
    }

    private boolean isBalance(List<Object> objects) {
        return retrieveStr(ACCOUNT_BALANCE, objects).matches("\\d+");
    }
}
