package org.example.service;

import org.example.model.Transaction;
import org.example.model.non_operating_income.AbstractNonOperatingIncome;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class NonOperatingIncomeService {

    LinkedList<Transaction> transactions;

    public NonOperatingIncomeService(ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService) {
        transactions = foreignCurrencyAccountantTableService.getTransactionsInForeignCurrency();
    }

    public AbstractNonOperatingIncome countIncome(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {

    }

    public List<Transaction> buildTransaction() {
    }
}

