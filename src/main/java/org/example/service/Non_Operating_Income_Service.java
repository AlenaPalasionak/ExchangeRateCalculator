package org.example.service;

import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AbstractNonOperatingIncome;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class NonOperatingIncomeService {

    LinkedList<Transaction> transactions;

    public NonOperatingIncomeService(ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService) {
        transactions = foreignCurrencyAccountantTableService.getTransactionsInForeignCurrency();
    }

//    public List<Transaction> buildTransaction() {
//    }

    public BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        return (rate1.subtract(rate2)).multiply(amount);
    }

    private boolean isEnoughForCommission(BigDecimal amount, BigDecimal commission) {
        return amount.compareTo(commission) >= 0;
    }

    private boolean isOutstandingAmountFullyPaid(Transaction transaction, List<Payment> payments) {
        BigDecimal outstandingAmount = transaction.getOutstandingAmount();
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(outstandingAmount);
    }

    public AbstractNonOperatingIncome getCarrierNonOperatingIncome() {

        return
    }

    public AbstractNonOperatingIncome getCommissionNonOperatingIncome() {

    }

    public AbstractNonOperatingIncome getNonOperatingIncomeFromCarrierSumWhenDoingPayment() {// возможно нужно чтобы принимал
        //Transaction, чтобы можно было возвращать один объект
        BigDecimal accumulatedPayments = BigDecimal.ZERO;
        BigDecimal nonOperatingIncome = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            LinkedList<Payment> incomingPayments = transaction.getIncomingPaymentList();
            boolean isOutstandingAmountFullyPaid = isOutstandingAmountFullyPaid(transaction, incomingPayments);
            BigDecimal commission = transaction.getCommission();
            BigDecimal actDateExchangeRate = transaction.getActDateExchangeRate().getRate();
            if (incomingPayments.size() > 1 & isOutstandingAmountFullyPaid) {
                for (Payment payment : incomingPayments) {
                    accumulatedPayments = accumulatedPayments.add(payment.getPaymentAmount());
                    if (accumulatedPayments.compareTo(commission) < 0) {
                        BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                        BigDecimal incomingPaymentAmountWithoutCommission = payment.getPaymentAmount()
                                .subtract(commission);
                        nonOperatingIncome = nonOperatingIncome.add((count(actDateExchangeRate, incomingPaymentRate
                                , incomingPaymentAmountWithoutCommission)));
                    } else {
                        BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                        BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                        nonOperatingIncome = nonOperatingIncome.add(count(actDateExchangeRate, incomingPaymentRate
                                , incomingPaymentAmount));
                    }
                }
            }
        }

        return nonOperatingIncome;// нужно еще определить ентри в зависимости - +
    }
}



