package org.example.service;

public class DataAggregatorService {
    private final CurrencyDifferenceService currencyDifferenceService;
    private final ForeignCurrencyTransactionService transactionService;

    public DataAggregatorService(CurrencyDifferenceService currencyDifferenceService,

                                 ForeignCurrencyTransactionService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.transactionService = transactionService;
    }


}
