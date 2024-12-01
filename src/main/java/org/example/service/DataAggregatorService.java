package org.example.service;

public class DataAggregatorService {
    private final RusRubExchangeIncomeService currencyDifferenceService;
    private final ForeignCurrencyAccountantTableService transactionService;

    public DataAggregatorService(RusRubExchangeIncomeService currencyDifferenceService,
                                 ForeignCurrencyAccountantTableService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.transactionService = transactionService;
    }


}
