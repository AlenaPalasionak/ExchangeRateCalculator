package org.example.service;

import org.example.constants.AccountantBookConstant;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.AccountantBookConstant.*;

public class DataAggregatorService {
    private final CurrencyDifferenceService currencyDifferenceService;
    private final TransactionService transactionService;

    public DataAggregatorService(CurrencyDifferenceService currencyDifferenceService,
                                 ExchangeRateService exchangeRateService,
                                 TransactionService transactionService) {
        this.currencyDifferenceService = currencyDifferenceService;
        this.exchangeRateService = exchangeRateService;
        this.transactionService = transactionService;
    }


}
