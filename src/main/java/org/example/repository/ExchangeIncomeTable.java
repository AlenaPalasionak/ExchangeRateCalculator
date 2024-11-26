package org.example.repository;

import org.example.model.Transaction;

import java.util.List;

public interface ExchangeIncomeTable {

    public void writeData(List<Transaction> transactions);
}
