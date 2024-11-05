package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.repository.ExchangeRateRepository;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.*;

import static org.example.constants.AccountantBookConstant.*;
import static org.example.constants.CurrencyConstant.*;

public class ForeignCurrencyTransactionService {

    private final Map<String, BigDecimal> exchangeRateCache;
    private final LinkedList<List<Object>> transactionTableInForeignCurrencyCache;
    private BigDecimal incomingPaymentAmount;

    public ForeignCurrencyTransactionService(ExchangeRateRepository exchangeRateRepository
            , AccountantBookService accountantBookService) {
        this.exchangeRateCache = exchangeRateRepository.getExchangeRateTableCache();
        this.transactionTableInForeignCurrencyCache = accountantBookService.getFilteredTableByCellContent
                (INCOMING_PAYMENT_AMOUNT, RUS_RUB, DOLLAR, EURO);
    }

    public LinkedList<Transaction> getTransactionsInForeignCurrency() {
        LinkedList<Transaction> transactionsInForeignCurrency = new LinkedList<>();

        for (List<Object> rowInForeignCurrency : this.transactionTableInForeignCurrencyCache) {
            Transaction transaction = createTransactionFromRow(rowInForeignCurrency);

            transactionsInForeignCurrency.add(transaction);
        }
        return transactionsInForeignCurrency;
    }

    private Transaction createTransactionFromRow(List<Object> rowObject) {
        LinkedList<Payment> incomingPayments = buildIncomingPayment(rowObject);
        LinkedList<Payment> outgoingPayments = buildOutgoingPayment(rowObject);
        boolean accountantBalance = isBalance(rowObject);
        String actDate = StringHelper.deleteLettersFromStringDate(String.valueOf(rowObject.get(ACT_DATE)));
        BigDecimal incomes = countIncomes(rowObject);
        String actNumber = String.valueOf(rowObject.get(ACT_NUMBER));
        ExchangeRate actDateExchangeRate = getExchangeRate(actDate);

        return new Transaction(incomingPayments, outgoingPayments, accountantBalance
                , actDate, incomes, actNumber, actDateExchangeRate);
    }

    private LinkedList<Payment> buildIncomingPayment(List<Object> rowObject) {
        LinkedHashMap<String, BigDecimal> paymentDateAndAmountMap;
        LinkedList<Payment> payments = new LinkedList<>();
        String paymentDateCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        paymentDateAndAmountMap = buildPaymentDateAndAmountMap(paymentDateCellString
                , paymentAmountCellString);
        String currency = StringHelper.retrieveLettersFromString(paymentAmountCellString);

        for (Map.Entry<String, BigDecimal> paymentDateAndAmountPair : paymentDateAndAmountMap.entrySet()) {
            BigDecimal paymentAmount = paymentDateAndAmountPair.getValue();
            String paymentDate = paymentDateAndAmountPair.getKey();
            ExchangeRate exchangeRate = getExchangeRate(paymentDateAndAmountPair.getKey());
            Payment payment = new Payment(paymentAmount, paymentDate, exchangeRate, currency);
            payments.add(payment);
        }

        return payments;
    }

    private LinkedList<Payment> buildOutgoingPayment(List<Object> rowObject) {
        LinkedHashMap<String, BigDecimal> paymentDateAndAmountMap;
        LinkedList<Payment> payments = new LinkedList<>();

        String paymentDateCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));
        paymentDateAndAmountMap = buildPaymentDateAndAmountMap(paymentDateCellString
                , paymentAmountCellString);
        String currency = StringHelper.retrieveLettersFromString(paymentAmountCellString);

        for (Map.Entry<String, BigDecimal> paymentDateAndAmountPair : paymentDateAndAmountMap.entrySet()) {
            BigDecimal paymentAmount = paymentDateAndAmountPair.getValue();
            String paymentDate = paymentDateAndAmountPair.getKey();
            ExchangeRate exchangeRate = getExchangeRate(paymentDateAndAmountPair.getKey());
            Payment payment = new Payment(paymentAmount, paymentDate, exchangeRate, currency);
            payments.add(payment);
        }

        return payments;
    }

    private LinkedHashMap<String, BigDecimal> buildPaymentDateAndAmountMap(String date, String amount) {
        LinkedHashMap<String, BigDecimal> dateAndAmountMap = new LinkedHashMap<>();
        BigDecimal paymentAmount = null;
        String paymentDate = null;
        if (date.contains("_")) {
            String[] payments = date.split("_");
            for (String payment : payments) {
                String[] dateAndAmount = payment.split(":");

try {

     String s = String.valueOf(dateAndAmount[0]);
    String cleanedInput = s.chars()
            .filter(ch -> Character.isDigit(ch) || ch == '.')
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    paymentAmount = new BigDecimal(cleanedInput);

    String s2 = String.valueOf(dateAndAmount[0]);
    String cleanedInput2 = s2.chars()
            .filter(ch -> Character.isDigit(ch) || ch == '.')
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    paymentAmount = new BigDecimal(cleanedInput);
    paymentDate = (cleanedInput2);
//                BigDecimal paymentAmount = StringHelper.retrieveNumberFromString
//                        (dateAndAmount[0]);
//                String paymentDate = StringHelper.deleteLettersFromStringDate
//                        (dateAndAmount[1]);
} catch (NumberFormatException e) {
    System.out.println("Ошибка формата числа: |"+ dateAndAmount[0] +"| или |" +dateAndAmount[1] +"|"+ e.getMessage());
//    for (char c : dateAndAmount[1].toCharArray()) {
//        System.out.println("Символ: '" + c + "' Код: " + (int) c);
//    }
}

                dateAndAmountMap.put(paymentDate, paymentAmount);

            }
        } else {
            dateAndAmountMap.put(StringHelper.deleteLettersFromStringDate(date)
                    , new BigDecimal(String.valueOf(StringHelper.retrieveNumberFromString(amount))));
        }

        return dateAndAmountMap;
    }

    private boolean isBalance(List<Object> rowObject) {
        return String.valueOf(rowObject.get(ACCOUNT_BALANCE)).matches("\\d+");
    }

    private BigDecimal countIncomes(List<Object> rowObject) {
        String incomingPaymentSumWithCurrency = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        String outgoingPaymentSumWithCurrency = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));

        BigDecimal income = StringHelper.retrieveNumberFromString(incomingPaymentSumWithCurrency);
        BigDecimal outgoings = StringHelper.retrieveNumberFromString(outgoingPaymentSumWithCurrency);
        return income.subtract(outgoings);
    }

    private ExchangeRate getExchangeRate(String paymentDate) {
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
