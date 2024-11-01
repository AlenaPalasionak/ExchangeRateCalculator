package org.example.service;

import org.example.constants.AccountantBookConstant;
import org.example.model.CurrencyDiff;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.repository.AccountantBookRepository;
import org.example.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.constants.AccountantBookConstant.*;
import static org.example.constants.AccountantBookConstant.OUTGOING_PAYMENT_SUM;

public class TransactionService {
    private ExchangeRateRepository exchangeRateRepository;
    private Map<String, BigDecimal> exchangeRateCache;


    public TransactionService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateCache = exchangeRateRepository.getExchangeRateTableCache();
    }

    private ExchangeRate createExchangeRate() {
        Map<String, BigDecimal> exchangeRateTable = exchangeRateRepository.getExchangeRateTableCache();
        for (Map.Entry<String, BigDecimal> datesAndRates : exchangeRateTable.entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate(datesAndRates.getKey(), datesAndRates.getValue());
        }

        return
    }

    public List<Transaction> getTransactions(int paymentSumIndex, String currency) {
        List<List<Object>> tableInRusRub = getTableInRusRubList(paymentSumIndex, currency);
        List<Transaction> transactionsInRusRub = new ArrayList<>();

        for (List<Object> rowInRusRub : tableInRusRub) {
            Transaction transaction = createTransactionFromRow(rowInRusRub);

            transactionsInRusRub.add(transaction);
        }
        return transactionsInRusRub;
    }

    private Transaction createTransactionFromRow(List<Object> rowObject) {
        Payment incomingPayment = createPaymentFromRow(rowObject, INCOMING_PAYMENT_SUM, INCOMING_PAYMENT_DATE);

        Payment outgoingPayment = createPaymentFromRow(rowObject, OUTGOING_PAYMENT_SUM, OUTGOING_PAYMENT_DATE);

        Payment(retrieveNum(OUTGOING_PAYMENT_SUM, rowObject));

        transaction.setOutgoingPaymentDate(retrieveStr(AccountantBookConstant.OUTGOING_PAYMENT_DATE, rowObject));

        transaction.setIncomes(countIncomes(incomingPaymentSum, outgoingPaymentSum));

        transaction.setActDate(retrieveStr(AccountantBookConstant.ACT_DATE, rowObject));

        transaction.setAccountBalance(isBalance(rowObject));

        transaction.setActNumber(retrieveStr(AccountantBookConstant.ACT_NUMBER, rowObject));
    }

    private Payment createPayment(List<Object> rowObject, int sum, int date, ExchangeRate exchangeRate) {

        BigDecimal paymentSum = retrieveNum(sum, rowObject);
        String paymentDate = retrieveStr(date, rowObject);

        Payment payment = new Payment(paymentSum, paymentDate, exchangeRate);

        ExchangeRate exchangeRate = createExchangeRateFromRow(rowObject, startIndex + 2);

        payment.setExchangeRate(exchangeRate);

        payment.setCurrency((String) rowObject.get(startIndex + 3));

        return payment;
    }

    private BigDecimal countIncomes(BigDecimal income, BigDecimal outgoings) {
        return income.subtract(outgoings);
    }



    private BigDecimal retrieveNum(int index, List<Object> objects) {
        return new BigDecimal(retrieveStr(index, objects).replaceAll("[а-я, А-Я]", ""));
    }//теперь у нас будет список с разными валютами и нужно сохранить название валютя, поэтому над этим методом нужно подумать

    private String retrieveStr(int index, List<Object> objects) {
        return objects.get(index).toString().replace("г\\.?", "").trim();
    }

    private boolean isBalance(List<Object> objects) {
        return retrieveStr(AccountantBookConstant.ACCOUNT_BALANCE, objects).matches("\\d+");
    }

//    public double calculateDifference(int paymentId, int exchangeRateId) {
//        // Логика расчета курсовой разницы
//
//    }

    public void saveCurrencyDiff(CurrencyDiff currencyDiff) {

        //      currencyDiffRepository.save(currencyDiff);

    }
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
