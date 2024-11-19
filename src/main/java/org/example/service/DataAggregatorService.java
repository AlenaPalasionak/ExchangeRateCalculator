package org.example.service;

public class DataAggregatorService {
    private final NonOperatingIncomeService currencyDifferenceService;
    private final ForeignCurrencyAccountantTableService transactionService;

    public DataAggregatorService(NonOperatingIncomeService currencyDifferenceService,
                                 ForeignCurrencyAccountantTableService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.transactionService = transactionService;
    }


}
