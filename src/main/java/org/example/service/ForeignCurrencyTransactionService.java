package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.repository.ExchangeRateRepository;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.constants.AccountantBookConstant.*;
import static org.example.constants.CurrencyConstant.*;

public class ForeignCurrencyTransactionService {

    private final Map<String, BigDecimal> exchangeRateCache;
    private final List<List<Object>> transactionTableInForeignCurrencyCache;

    public ForeignCurrencyTransactionService(ExchangeRateRepository exchangeRateRepository
            , AccountantBookService accountantBookService) {
        this.exchangeRateCache = exchangeRateRepository.getExchangeRateTableCache();
        this.transactionTableInForeignCurrencyCache = accountantBookService.getFilteredTableByCellContent
                (INCOMING_PAYMENT_SUM, RUS_RUB, DOLLAR, EURO);
    }

    public List<Transaction> getTransactionsInForeignCurrency() {
        List<Transaction> transactionsInForeignCurrency = new ArrayList<>();

        for (List<Object> rowInForeignCurrency : this.transactionTableInForeignCurrencyCache) {
            Transaction transaction = createTransactionFromRow(rowInForeignCurrency);

            transactionsInForeignCurrency.add(transaction);
        }
        return transactionsInForeignCurrency;
    }

    private Transaction createTransactionFromRow(List<Object> rowObject) {
        Payment incomingPayment = getIncomingPayment(rowObject);
        Payment outgoingPayment = getOutGoingPayment(rowObject);
        boolean accountantBalance = isBalance(rowObject);
        String actDate = String.valueOf(rowObject.get(ACT_DATE));
        BigDecimal incomes = countIncomes(rowObject);
        String actNumber = String.valueOf(rowObject.get(ACT_NUMBER));
        ExchangeRate actDateExchangeRate = getExchangeRate(rowObject, actDate);

        return new Transaction(incomingPayment, outgoingPayment, accountantBalance
                , actDate, incomes, actNumber, actDateExchangeRate);
    }

    private Payment getIncomingPayment(List<Object> rowObject) {
        BigDecimal incomingPaymentSum = StringHelper.retrieveNumberFromString(rowObject, INCOMING_PAYMENT_SUM);
        String incomingPaymentDate = StringHelper.deleteYearSignFromStringDate(rowObject, INCOMING_PAYMENT_DATE);
        String currency = StringHelper.retrieveLettersFromString(rowObject, INCOMING_PAYMENT_SUM);

        return new Payment(incomingPaymentSum, incomingPaymentDate, getExchangeRate(rowObject
                , incomingPaymentDate), currency);
    }

    private Payment getOutGoingPayment(List<Object> rowObject) {
        BigDecimal outGoingPaymentSum = StringHelper.retrieveNumberFromString(rowObject, OUTGOING_PAYMENT_SUM);
        String outGoingPaymentDate = StringHelper.deleteYearSignFromStringDate(rowObject, OUTGOING_PAYMENT_DATE);
        String currency = StringHelper.retrieveLettersFromString(rowObject, OUTGOING_PAYMENT_SUM);

        return new Payment(outGoingPaymentSum, outGoingPaymentDate, getExchangeRate(rowObject
                , outGoingPaymentDate), currency);
    }

    private boolean isBalance(List<Object> rowObject) {
        return String.valueOf(rowObject.get(ACCOUNT_BALANCE)).matches("\\d+");
    }

    private BigDecimal countIncomes(List<Object> rowObject) {
        BigDecimal income = StringHelper.retrieveNumberFromString(rowObject, INCOMING_PAYMENT_SUM);
        BigDecimal outgoings = StringHelper.retrieveNumberFromString(rowObject, OUTGOING_PAYMENT_SUM);
        return income.subtract(outgoings);
    }

    private ExchangeRate getExchangeRate(List<Object> rowObject, String paymentDate) {
        BigDecimal rate = exchangeRateCache.get(paymentDate);
        return new ExchangeRate(paymentDate, rate);
    }



//    public double calculateDifference(int paymentId, int exchangeRateId) {
//        // Логика расчета курсовой разницы
//
//    }

//    public void saveCurrencyDiff(CurrencyDiff currencyDiff) {
//
//        //      currencyDiffRepository.save(currencyDiff);
//
//    }
    //Service Pattern: Сервисный класс PaymentService отвечает за бизнес-логику.
    // инкапсулирует бизнес-логику и взаимодействует с репозиториями.

    // Он использует репозитории для получения данных о
    // платежах и курсах, а также для сохранения курсовой разницы.

    // PaymentService использует PaymentRepository и ExchangeRateRepository
    // для получения необходимых данных о платежах и курсах.

    //методы. принимает идентификаторы платежа и курса,
    // выполняет необходимые расчеты и создает объект CurrencyDiff.
    //+ calculateDifference(paymentId: int, exchangeRateId: int): double |
    //
    //| + saveCurrencyDiff(currencyDiff: CurrencyDiff): void |
}
