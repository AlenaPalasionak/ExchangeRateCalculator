package org.example.service;

public class DataAggregatorService {
    private final CurrencyDifferenceService currencyDifferenceService;
    private final ForeignCurrencyTransactionService transactionService;

    public DataAggregatorService(CurrencyDifferenceService currencyDifferenceService,
                                 ExchangeRateService exchangeRateService,
                                 ForeignCurrencyTransactionService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
       // this.exchangeRateService = exchangeRateService;
        this.transactionService = transactionService;
    }


}
