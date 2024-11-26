package org.example.service;

import org.example.constants.AccountantBookConstants;
import org.example.constants.JournalEntryConstants;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.*;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.example.constants.AccountantBookConstants.*;
import static org.example.constants.AccountantBookConstants.ACT_NUMBER;
import static org.example.constants.CurrencyConstants.*;

public class ExchangeIncomeService {
    ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService;
    private final ExchangeRateTableService exchangeRateService;
    private final LinkedList<List<Object>> transactionTableInForeignCurrency;

    public ExchangeIncomeService(ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService, ExchangeRateTableService exchangeRateService) {
        this.foreignCurrencyAccountantTableService = foreignCurrencyAccountantTableService;
        this.transactionTableInForeignCurrency = foreignCurrencyAccountantTableService.getFilteredTableByCellContent
                (INCOMING_PAYMENT_AMOUNT, RUS_RUB, DOLLAR, EURO);
        this.exchangeRateService = exchangeRateService;
    }

    public LinkedList<Transaction> getTransactions() {
        LinkedList<Transaction> transactionsInForeignCurrency = new LinkedList<>();

        for (List<Object> rowInForeignCurrency : this.transactionTableInForeignCurrency) {
            Transaction transaction = createTransaction(rowInForeignCurrency);

            transactionsInForeignCurrency.add(transaction);
        }

        return transactionsInForeignCurrency;
    }

    private Transaction createTransaction(List<Object> rowObject) {
        BigDecimal receivableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT)));
        BigDecimal payableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT)));

        LinkedList<Payment> incomingPayments = foreignCurrencyAccountantTableService.buildIncomingPayment(rowObject);
        LinkedList<Payment> outgoingPayments = foreignCurrencyAccountantTableService.buildOutgoingPayment(rowObject);
        boolean accountantBalance = foreignCurrencyAccountantTableService.isBalance(rowObject);
        String actDate = StringHelper.retrieveDateFromString(String.valueOf(rowObject.get(ACT_DATE)));
        BigDecimal commissionAmount = foreignCurrencyAccountantTableService.countCommission(rowObject);
        String actNumber = String.valueOf(rowObject.get(ACT_NUMBER));
        ExchangeRate actDateExchangeRate = exchangeRateService.getExchangeRate(actDate);
        BigDecimal actDateExchangeRateAmount = exchangeRateService.getExchangeRateAmount(actDate);

        ActPaymentExchangeIncome actPaymentExchangeIncome = buildActPaymentExchangeIncome
                (incomingPayments, commissionAmount, actDateExchangeRateAmount, payableAmount, receivableAmount);

        CommissionExchangeIncome commissionExchangeIncome = buildCommissionExchangeIncome(incomingPayments
                , commissionAmount, actDateExchangeRateAmount, receivableAmount);
        ReceivedPaidExchangeIncome receivedPaidExchangeIncome = buildReceivedPaidExchangeIncome();
        AccountExchangeIncome accountExchangeIncome = buildAccountExchangeIncome();

        return new Transaction(receivableAmount, payableAmount, incomingPayments, outgoingPayments, accountantBalance
                , actDate, commissionAmount, actNumber, actDateExchangeRate, actPaymentExchangeIncome
                , commissionExchangeIncome, receivedPaidExchangeIncome, accountExchangeIncome);
    }

    private BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        return (rate1.subtract(rate2)).multiply(amount);
    }

    private boolean isReceivableAmountFullyPaid(BigDecimal receivableAmount, List<Payment> payments) {
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(receivableAmount);
    }

    private ActPaymentExchangeIncome buildActPaymentExchangeIncome(LinkedList<Payment> incomingPayments
            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, BigDecimal payableAmount
            , BigDecimal receivableAmount) {

        ActPaymentExchangeIncome actPaymentExchangeIncome = new ActPaymentExchangeIncome();

        boolean isReceivableAmountFullyPaid = isReceivableAmountFullyPaid(receivableAmount, incomingPayments);
        BigDecimal accumulatedPayments = BigDecimal.ZERO;
        BigDecimal actPaymentExchangeIncomeAmount = BigDecimal.ZERO;
        boolean isCommissionSubtracted = false;

        if (incomingPayments.size() > 1 && isReceivableAmountFullyPaid) {
            for (Payment payment : incomingPayments) {//пойдем по всем платежам
                BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                accumulatedPayments = accumulatedPayments.add(incomingPaymentAmount);//складываем всеплатежи тут
                if (accumulatedPayments.compareTo(commissionAmount) > 0) {// если платежи привысили комиссию
                    if (!isCommissionSubtracted) {// если комиссия не вычтена из платежей
                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                        isCommissionSubtracted = true;
                    }//коммиссия вычтена
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    actPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmount);
                    actPaymentExchangeIncomeAmount = actPaymentExchangeIncomeAmount.add(actPaymentExchangeIncomeAmount);
                }//конец если платежи привысили комиссию
            }
        } else {// если платеж был 1
            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getPaymentAmount();
            actPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , payableAmount);
            actPaymentExchangeIncomeAmount = actPaymentExchangeIncomeAmount.add(actPaymentExchangeIncomeAmount);
        }

        actPaymentExchangeIncome.setIncome(actPaymentExchangeIncomeAmount);
        if (isPositiveOrZero(actPaymentExchangeIncomeAmount)) {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
        } else {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
        }

        return actPaymentExchangeIncome;
    }

    private CommissionExchangeIncome buildCommissionExchangeIncome(LinkedList<Payment> incomingPayments
            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, BigDecimal receivableAmount) {
        CommissionExchangeIncome commissionIncome = new CommissionExchangeIncome();
//(курс даты акта – курс даты опл. Нам)* сумма комиссии
        boolean isOutstandingAmountFullyPaid = isReceivableAmountFullyPaid(receivableAmount, incomingPayments);

        if (incomingPayments.size() > 1 & isOutstandingAmountFullyPaid) {
            BigDecimal accumulatedPayments = BigDecimal.ZERO;
            BigDecimal commissionExchangeIncomeAmount = BigDecimal.ZERO;
            for (Payment payment : incomingPayments) {
                accumulatedPayments = accumulatedPayments.add(payment.getPaymentAmount());
                if (accumulatedPayments.compareTo(commissionAmount) < 0) {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmountWithoutCommission = payment.getPaymentAmount()
                            .subtract(commissionAmount);
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add((count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmountWithoutCommission)));
                } else {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmount));
                }
            }

            commissionIncome.setIncome(commissionExchangeIncomeAmount);
            if (isPositiveOrZero(commissionExchangeIncomeAmount)) {
                commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
            } else {
                commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
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



