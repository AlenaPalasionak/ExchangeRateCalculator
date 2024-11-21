package org.example.service;

import org.example.constants.JournalEntryConstants;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AbstractNonOperatingIncome;
import org.example.model.non_operating_income.FromBeingPayedNonOperatingIncome;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class NonOperatingIncomeService {

    LinkedList<Transaction> transactions;

    public NonOperatingIncomeService(ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService) {
        transactions = foreignCurrencyAccountantTableService.getTransactionsInForeignCurrency();
    }

    public List<Transaction> buildTransaction() {
        for (Transaction transaction : transactions) {
            transaction.setFromBeingPayedIncome(buildFromBeingPayedIncome(transaction));
        }
    }

    public BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        return (rate1.subtract(rate2)).multiply(amount);
    }

    private boolean isOutstandingAmountFullyPaid(Transaction transaction, List<Payment> payments) {
        BigDecimal outstandingAmount = transaction.getOutstandingAmount();
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(outstandingAmount);
    }

//    public AbstractNonOperatingIncome getCarrierNonOperatingIncome() {
//
//        return
//    }
//
//    public AbstractNonOperatingIncome getCommissionNonOperatingIncome() {
//
//    }

    private AbstractNonOperatingIncome buildFromBeingPayedIncome(Transaction transaction) {// возможно нужно чтобы принимал

        AbstractNonOperatingIncome fromBeingPayedIncome = new FromBeingPayedNonOperatingIncome();

        LinkedList<Payment> incomingPayments = transaction.getIncomingPaymentList();
        boolean isOutstandingAmountFullyPaid = isOutstandingAmountFullyPaid(transaction, incomingPayments);
        BigDecimal commission = transaction.getCommission();
        BigDecimal actDateExchangeRate = transaction.getActDateExchangeRate().getRate();

        if (incomingPayments.size() > 1 & isOutstandingAmountFullyPaid) {
            BigDecimal accumulatedPayments = BigDecimal.ZERO;
            BigDecimal nonOperatingIncomeAmount = BigDecimal.ZERO;
            for (Payment payment : incomingPayments) {
                accumulatedPayments = accumulatedPayments.add(payment.getPaymentAmount());
                if (accumulatedPayments.compareTo(commission) < 0) {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmountWithoutCommission = payment.getPaymentAmount()
                            .subtract(commission);
                    nonOperatingIncomeAmount = nonOperatingIncomeAmount.add((count(actDateExchangeRate, incomingPaymentRate
                            , incomingPaymentAmountWithoutCommission)));
                } else {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                    nonOperatingIncomeAmount = nonOperatingIncomeAmount.add(count(actDateExchangeRate, incomingPaymentRate
                            , incomingPaymentAmount));
                }
            }

            fromBeingPayedIncome.setIncome(nonOperatingIncomeAmount);
            if (nonOperatingIncomeAmount.compareTo(BigDecimal.ZERO) >= 0) {
                fromBeingPayedIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
            } else {
                fromBeingPayedIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
            }
        }

        return fromBeingPayedIncome;
    }
}



