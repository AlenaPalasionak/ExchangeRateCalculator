package org.example.service;

import org.example.constants.JournalEntryConstants;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AbstractExchangeIncome;
import org.example.model.non_operating_income.ActPaymentExchangeIncome;
import org.example.model.non_operating_income.CommissionExchangeIncome;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class ExchangeIncomeService {
    ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService;
    LinkedList<Transaction> transactions;

    public ExchangeIncomeService(ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService) {
        this.foreignCurrencyAccountantTableService = foreignCurrencyAccountantTableService;
    }

    public List<Transaction> completeTransactionBuilding() {
        LinkedList<Transaction> transactions = foreignCurrencyAccountantTableService.getTransactionsInForeignCurrency();
        for (Transaction transaction : transactions) {
            transaction.setActPaymentExchangeIncome(buildActPaymentExchangeIncome(transaction));
            transaction.setCommissionExchangeIncome(buildCommissionExchangeIncome(transaction));
            transaction.setReceivedPaidExchangeIncome(buildReceivedPaidExchangeIncome(transaction));
            transaction.setAccountExchangeIncome(buildAccountExchangeIncome(transaction));
        }
        return transactions;
    }

    public BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        return (rate1.subtract(rate2)).multiply(amount);
    }

    private boolean isReceivableAmountFullyPaid(Transaction transaction, List<Payment> payments) {
        BigDecimal receivableAmount = transaction.getReceivableAmount();
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(receivableAmount);
    }

    private AbstractExchangeIncome buildActPaymentExchangeIncome(Transaction transaction) {// возможно нужно чтобы принимал

        AbstractExchangeIncome actPaymentExchangeIncome = new ActPaymentExchangeIncome();

        LinkedList<Payment> incomingPayments = transaction.getIncomingPaymentList();
        boolean isReceivableAmountFullyPaid = isReceivableAmountFullyPaid(transaction, incomingPayments);
        BigDecimal commissionAmount = transaction.getCommission();
        BigDecimal actDateExchangeRate = transaction.getActDateExchangeRate().getRate();
        BigDecimal payableAmount = transaction.getPayableAmount();
        BigDecimal accumulatedPayments = BigDecimal.ZERO;
        BigDecimal actPaymentExchangeIncomeAmount = BigDecimal.ZERO;
        boolean isCommissionPayed = false;
        if (incomingPayments.size() > 1 && isReceivableAmountFullyPaid) {
            for (Payment payment : incomingPayments) {
                BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                accumulatedPayments = accumulatedPayments.add(incomingPaymentAmount);
                if (accumulatedPayments.compareTo(commissionAmount) > 0) {
                    if (!isCommissionPayed) {
                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                        isCommissionPayed = true;
                    }
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    actPaymentExchangeIncomeAmount = actPaymentExchangeIncomeAmount.add((count(actDateExchangeRate, incomingPaymentRate
                            , incomingPaymentAmount)));
                } else continue;
            }
        } else {
            BigDecimal incomingPaymentRate = incomingPayments.get(0).getPaymentAmount();
            actPaymentExchangeIncomeAmount = actPaymentExchangeIncomeAmount.add((count(actDateExchangeRate, incomingPaymentRate
                    , payableAmount)));
        }
        actPaymentExchangeIncome.setIncome(actPaymentExchangeIncomeAmount);
        if (isPositiveOrZero(actPaymentExchangeIncomeAmount)) {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
        } else {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
        }

        return actPaymentExchangeIncome;
    }

    private AbstractExchangeIncome buildCommissionExchangeIncome(Transaction transaction) {
        AbstractExchangeIncome commissionIncome = new CommissionExchangeIncome();
//(курс даты акта – курс даты опл. Нам)* сумма комиссии
        LinkedList<Payment> incomingPayments = transaction.getIncomingPaymentList();
        boolean isOutstandingAmountFullyPaid = isReceivableAmountFullyPaid(transaction, incomingPayments);
        BigDecimal commission = transaction.getCommission();
        BigDecimal actDateExchangeRate = transaction.getActDateExchangeRate().getRate();

        if (incomingPayments.size() > 1 & isOutstandingAmountFullyPaid) {
            BigDecimal accumulatedPayments = BigDecimal.ZERO;
            BigDecimal commissionExchangeIncomeAmount = BigDecimal.ZERO;
            for (Payment payment : incomingPayments) {
                accumulatedPayments = accumulatedPayments.add(payment.getPaymentAmount());
                if (accumulatedPayments.compareTo(commission) < 0) {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmountWithoutCommission = payment.getPaymentAmount()
                            .subtract(commission);
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add((count(actDateExchangeRate, incomingPaymentRate
                            , incomingPaymentAmountWithoutCommission)));
                } else {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(count(actDateExchangeRate, incomingPaymentRate
                            , incomingPaymentAmount));
                }
            }

            actPaymentExchangeIncome.setIncome(commissionExchangeIncomeAmount);
            if (isPositiveOrZero(commissionExchangeIncomeAmount)) {
                actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
            } else {
                actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
            }
        }

        return commissionIncome;
    }

    private AbstractExchangeIncome buildReceivedPaidExchangeIncome(Transaction transaction) {

    }

    private AbstractExchangeIncome buildAccountExchangeIncome(Transaction transaction) {

    }

    private boolean isPositiveOrZero(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) >= 0;
    }
}



