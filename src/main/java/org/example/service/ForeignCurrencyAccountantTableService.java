package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.repository.AccountantTableImpl;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.example.constants.AccountantBookConstants.*;

public class ForeignCurrencyAccountantTableService extends AccountantTableService {

    private final ExchangeRateTableService exchangeRateService;

    public ForeignCurrencyAccountantTableService() {
        super(new AccountantTableImpl());
        exchangeRateService = new ExchangeRateTableService();
    }

    LinkedList<Payment> buildIncomingPayment(List<Object> rowObject) {
        LinkedList<Payment> payments;
        String paymentDateCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        payments = buildPayment(paymentDateCellString, paymentAmountCellString);

        return payments;
    }

    LinkedList<Payment> buildOutgoingPayment(List<Object> rowObject) {
        LinkedList<Payment> payments;

        String paymentDateCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_DATE));
        String paymentAmountCellString = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));
        payments = buildPayment(paymentDateCellString, paymentAmountCellString);

        return payments;
    }

    LinkedHashMap<String, BigDecimal> buildPaymentDateAndAmountMap(String date, String amount) {
        LinkedHashMap<String, BigDecimal> dateAndAmountMap = new LinkedHashMap<>();
        BigDecimal paymentAmount = null;
        String paymentDate = null;
        if (date.contains("_")) {
            String[] payments = date.split("_");
            for (String payment : payments) {
                String[] dateAndAmount = payment.split(":");
                int paymentAmountIndex = 1;
                int paymentDateIndex = 0;
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

    LinkedList<Payment> buildPayment(String paymentDateCellString, String paymentAmountCellString) {
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

    boolean isBalance(List<Object> rowObject) {
        return String.valueOf(rowObject.get(ACCOUNT_BALANCE)).matches("\\d+");
    }

    BigDecimal countCommission(List<Object> rowObject) {
        String incomingPaymentSumWithCurrency = String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT));
        String outgoingPaymentSumWithCurrency = String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT));

        BigDecimal commission = StringHelper.retrieveNumberFromString(incomingPaymentSumWithCurrency);
        BigDecimal outgoings = StringHelper.retrieveNumberFromString(outgoingPaymentSumWithCurrency);
        return commission.subtract(outgoings);
    }
}
