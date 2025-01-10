package org.example.repository;

import org.example.model.FreightJournalRecord;

import java.util.List;

public interface ExchangeIncomeTable {

    public void writeData(List<FreightJournalRecord> sourceTableData);
}
