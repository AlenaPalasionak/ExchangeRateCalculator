package org.example.service;

public class DataAggregatorService {
    private final ExchangeIncomeService currencyDifferenceService;
    private final ForeignCurrencyAccountantTableService transactionService;

    public DataAggregatorService(ExchangeIncomeService currencyDifferenceService,
                                 ForeignCurrencyAccountantTableService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.transactionService = transactionService;
    }


}
