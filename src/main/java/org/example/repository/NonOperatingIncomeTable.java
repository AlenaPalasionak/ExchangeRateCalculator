package org.example.repository;

import org.example.model.Transaction;

import java.util.List;

public interface NonOperatingIncomeTable {

    public void writeData(List<Transaction> transactions);
}
