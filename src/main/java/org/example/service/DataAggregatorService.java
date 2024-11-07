package org.example.service;

public class DataAggregatorService {
    private final Non_Operating_Income_Service currencyDifferenceService;
    private final ForeignCurrencyAccountantTableService transactionService;

    public DataAggregatorService(Non_Operating_Income_Service currencyDifferenceService,
                                 ForeignCurrencyAccountantTableService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.transactionService = transactionService;
    }


}
