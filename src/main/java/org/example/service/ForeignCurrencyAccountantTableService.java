package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.repository.AccountantTableImpl;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.example.constants.AccountantBookConstants.*;
import static org.example.constants.CurrencyConstants.*;

public class ForeignCurrencyAccountantTableService extends AccountantTableService {
    private final ExchangeRateTableService exchangeRateService;
    private final LinkedList<List<Object>> transactionTableInForeignCurrency;

    public ForeignCurrencyAccountantTableService(ExchangeRateTableService exchangeRateService) {
        super(new AccountantTableImpl());
        this.exchangeRateService = exchangeRateService;
        this.transactionTableInForeignCurrency = getFilteredTableByCellContent
                (INCOMING_PAYMENT_AMOUNT, RUS_RUB, DOLLAR, EURO);
    }

    public LinkedList<Transaction> getTransactionsInForeignCurrency() {
        LinkedList<Transaction> transactionsInForeignCurrency = new LinkedList<>();

        for (List<Object> rowInForeignCurrency : this.transactionTableInForeignCurrency) {
            Transaction transaction = createTransactionFromRow(rowInForeignCurrency);

            transactionsInForeignCurrency.add(transaction);
        }

        return transactionsInForeignCurrency;
    }

    private Transaction createTransactionFromRow(List<Object> rowObject) {
        BigDecimal receivableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT)));
        BigDecimal payableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT)));

        LinkedList<Payment> incomingPayments = buildIncomingPayment(rowObject);
        LinkedList<Payment> outgoingPayments = buildOutgoingPayment(rowObject);
        boolean accountantBalance = isBalance(rowObject);
        String actDate = StringHelper.retrieveDateFromString(String.valueOf(rowObject.get(ACT_DATE)));
        BigDecimal commission = countCommission(rowObject);
        String actNumber = String.valueOf(rowObject.get(ACT_NUMBER));
        ExchangeRate actDateExchangeRate = exchangeRateService.getExchangeRate(actDate);

        return new Transaction(receivableAmount, payableAmount, incomingPayments, outgoingPayments, accountantBalance
                , actDate, commission, actNumber, actDateExchangeRate);
    }

    private LinkedList<Payment> buildIncomingPayment(List<Object> rowObject) {
        LinkedList<Payment> payments;
        String paymentDateCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        payments = buildPayment(paymentDateCellString, paymentAmountCellString);

        return payments;
    }

    private LinkedList<Payment> buildOutgoingPayment(List<Object> rowObject) {
        LinkedList<Payment> payments;

        String paymentDateCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));
        payments = buildPayment(paymentDateCellString, paymentAmountCellString);

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
                int paymentAmountIndex = 0;
                int paymentDateIndex = 1;
                paymentAmount = new BigDecimal(dateAndAmount[paymentAmountIndex]
                        .replaceAll("[^0-9]", ""));
                paymentDate = StringHelper.retrieveDateFromString(dateAndAmount[paymentDateIndex]);
//Log
            }
            dateAndAmountMap.put(paymentDate, paymentAmount);
        } else {
            paymentAmount = new BigDecimal(String.valueOf(StringHelper.retrieveNumberFromString(amount)));
            paymentDate = StringHelper.retrieveDateFromString(date);
            dateAndAmountMap.put(paymentDate, paymentAmount);
        }

        return dateAndAmountMap;
    }

    private LinkedList<Payment> buildPayment(String paymentDateCellString, String paymentAmountCellString) {
        LinkedHashMap<String, BigDecimal> paymentDateAndAmountMap;
        LinkedList<Payment> payments = new LinkedList<>();

        paymentDateAndAmountMap = buildPaymentDateAndAmountMap(paymentDateCellString
                , paymentAmountCellString);
        String currency = StringHelper.retrieveLettersFromString(paymentAmountCellString);

        for (Map.Entry<String, BigDecimal> paymentDateAndAmountPair : paymentDateAndAmountMap.entrySet()) {
            BigDecimal paymentAmount = paymentDateAndAmountPair.getValue();
            String paymentDate = paymentDateAndAmountPair.getKey();
            ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(paymentDateAndAmountPair.getKey());
            Payment payment = new Payment(paymentAmount, paymentDate, exchangeRate, currency);
            payments.add(payment);
        }

        return payments;
    }

    private boolean isBalance(List<Object> rowObject) {
        return String.valueOf(rowObject.get(ACCOUNT_BALANCE)).matches("\\d+");
    }

    private BigDecimal countCommission(List<Object> rowObject) {
        String incomingPaymentSumWithCurrency = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        String outgoingPaymentSumWithCurrency = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));

        BigDecimal commission = StringHelper.retrieveNumberFromString(incomingPaymentSumWithCurrency);
        BigDecimal outgoings = StringHelper.retrieveNumberFromString(outgoingPaymentSumWithCurrency);
        return commission.subtract(outgoings);
    }
}
