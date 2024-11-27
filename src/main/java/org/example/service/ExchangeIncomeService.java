package org.example.service;

import org.example.constants.JournalEntryConstants;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AccountExchangeIncome;
import org.example.model.non_operating_income.CommissionExchangeIncome;
import org.example.model.non_operating_income.CompletionCertificateVSPaymentExchangeIncome;
import org.example.model.non_operating_income.ReceivedVSPaidExchangeIncome;
import org.example.util.StringHelper;

import java.math.BigDecimal;
import java.util.*;

import static org.example.constants.AccountantBookConstants.*;
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

        CompletionCertificateVSPaymentExchangeIncome actPaymentExchangeIncome = completionCertificateVSPaymentExchangeIncome
                (incomingPayments, commissionAmount, actDateExchangeRateAmount, payableAmount, receivableAmount);

        CommissionExchangeIncome commissionExchangeIncome = buildCommissionExchangeIncome(incomingPayments
                , commissionAmount, actDateExchangeRateAmount, receivableAmount);
        ReceivedVSPaidExchangeIncome receivedPaidExchangeIncome = buildReceivedVSPaidExchangeIncome();
        AccountExchangeIncome accountExchangeIncome = buildAccountExchangeIncome();

        return new Transaction(receivableAmount, payableAmount, incomingPayments, outgoingPayments, accountantBalance
                , actDate, commissionAmount, actNumber, actDateExchangeRate, actPaymentExchangeIncome
                , commissionExchangeIncome, receivedPaidExchangeIncome, accountExchangeIncome);
    }

    private BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        return (rate1.subtract(rate2)).multiply(amount);
    }

    private boolean isAmountFullyPaid(BigDecimal amount, List<Payment> payments) {
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(amount);
    }

    private CompletionCertificateVSPaymentExchangeIncome completionCertificateVSPaymentExchangeIncome(LinkedList<Payment> incomingPayments
            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, BigDecimal payableAmount
            , BigDecimal receivableAmount) {

        CompletionCertificateVSPaymentExchangeIncome actPaymentExchangeIncome = new CompletionCertificateVSPaymentExchangeIncome();

        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);
        BigDecimal accumulatedPayments = BigDecimal.ZERO;
        BigDecimal completionCertificateVSPaymentExchangeIncomeAmount = BigDecimal.ZERO;
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
                    completionCertificateVSPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmount);
                    completionCertificateVSPaymentExchangeIncomeAmount = completionCertificateVSPaymentExchangeIncomeAmount.add(completionCertificateVSPaymentExchangeIncomeAmount);
                }//конец если платежи привысили комиссию
            }
        } else {// если платеж был 1
            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
            completionCertificateVSPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , payableAmount);
            completionCertificateVSPaymentExchangeIncomeAmount = completionCertificateVSPaymentExchangeIncomeAmount.add(completionCertificateVSPaymentExchangeIncomeAmount);
        }

        actPaymentExchangeIncome.setIncome(completionCertificateVSPaymentExchangeIncomeAmount);
        if (isPositiveOrZero(completionCertificateVSPaymentExchangeIncomeAmount)) {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_60_11);
        } else {
            actPaymentExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_62_11);
        }

        return actPaymentExchangeIncome;
    }

    private CommissionExchangeIncome buildCommissionExchangeIncome(LinkedList<Payment> incomingPayments
            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, BigDecimal receivableAmount) {
        //(курс даты акта – курс даты опл. Нам)* сумма комиссии
        CommissionExchangeIncome commissionIncome = new CommissionExchangeIncome();

        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);
        BigDecimal accumulatedPayments = BigDecimal.ZERO;
        BigDecimal commissionExchangeIncomeAmount = BigDecimal.ZERO;

        if (incomingPayments.size() > 1 & isReceivableAmountFullyPaid) {

            for (Payment payment : incomingPayments) {
                BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                accumulatedPayments = accumulatedPayments.add(incomingPaymentAmount);
                if (accumulatedPayments.compareTo(commissionAmount) <= 0) {
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmount);
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(commissionExchangeIncomeAmount);
                } else { //конец если меньше чем комиссия заплатили
                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                    commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                            , commissionAmount);
                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(commissionAmount);
                }
            }

            commissionIncome.setIncome(commissionExchangeIncomeAmount);
            if (isPositiveOrZero(commissionExchangeIncomeAmount)) {
                commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_90_7);
            } else {
                commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_90_4_62_11);
            }
        }

        return commissionIncome;
    }

    private boolean isPositiveOrZero(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) >= 0;
    }

    private ReceivedVSPaidExchangeIncome buildReceivedVSPaidExchangeIncome(LinkedList<Payment> incomingPayments
            , LinkedList<Payment> outgoingPayments, BigDecimal receivableAmount
            , BigDecimal payableAmount, BigDecimal commissionAmount) {

        ReceivedVSPaidExchangeIncome receivedVSPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();

        boolean isPayableAmountFullyPaid = isAmountFullyPaid(payableAmount, outgoingPayments);
        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);

        BigDecimal receivedVSPaidExchangeIncomeAmount = BigDecimal.ZERO;

        BigDecimal accumulatedIncomingPayments = BigDecimal.ZERO;
        boolean isCommissionSubtracted = false;

        if (isPayableAmountFullyPaid && isReceivableAmountFullyPaid) { //считаем, только если выплатили до конца

            if (incomingPayments.size() > 1 && outgoingPayments.size() == 1) {
                BigDecimal outgoingPaymentRate = outgoingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
                for (Payment payment : incomingPayments) {//пойдем по всем платежам
                    BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
                    accumulatedIncomingPayments = accumulatedIncomingPayments.add(incomingPaymentAmount);//складываем всеплатежи тут
                    if (accumulatedIncomingPayments.compareTo(commissionAmount) > 0) {// если платежи привысили комиссию
                        if (!isCommissionSubtracted) {// если комиссия не вычтена из платежей
                            incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                            isCommissionSubtracted = true;
                        }//коммиссия вычтена
                        BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
                        receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRate, outgoingPaymentRate
                                , incomingPaymentAmount));
                    }
                }
            } else if (incomingPayments.size() == 1 && outgoingPayments.size() == 1) {
                BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
                BigDecimal outgoingPaymentRate = outgoingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
                receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRate
                        , outgoingPaymentRate, payableAmount));
            } else if (incomingPayments.size() == 1 && outgoingPayments.size() > 1) {
                BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
                for (Payment payment : outgoingPayments) {//пойдем по всем платежам
                    BigDecimal outgoingPaymentAmount = payment.getPaymentAmount();
                    BigDecimal outgoingPaymentRate = payment.getExchangeRate().getRate();
                    receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add
                            (count(incomingPaymentRate, outgoingPaymentRate, outgoingPaymentAmount));
                }
            } else if (incomingPayments.size() > 1 && outgoingPayments.size() > 1) {
                Payment incomingPayment = null;
                Payment outgoingPayment = null;

                Map<BigDecimal, BigDecimal> remainderMap = new LinkedHashMap<>();
                Iterator<Payment> incomingIterator = incomingPayments.iterator();
                Iterator<Payment> outgoingIterator = outgoingPayments.iterator();//пойдем по всем платежам

                while (incomingIterator.hasNext() || outgoingIterator.hasNext()) {
                    BigDecimal incomingPaymentAmount;
                    BigDecimal outgoingPaymentAmount = BigDecimal.ZERO;

                    BigDecimal outgoingPaymentRate = outgoingPayments.get(outgoingPayments.size() - 1).getExchangeRate().getRate();// курс последнего платежа
                    if (outgoingIterator.hasNext()) {
                        outgoingPayment = outgoingIterator.next();
                        outgoingPaymentAmount = outgoingPayment.getPaymentAmount();
                        outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
                    }
                    if (incomingIterator.hasNext()) {
                        incomingPayment = incomingIterator.next();
                        incomingPaymentAmount = incomingPayment.getPaymentAmount();
                        accumulatedIncomingPayments = accumulatedIncomingPayments.add(incomingPaymentAmount);//складываем всеплатежи тут
                        BigDecimal incomingPaymentRate = incomingPayment.getExchangeRate().getRate();

                        if (accumulatedIncomingPayments.compareTo(commissionAmount) > 0) {// если платежи привысили комиссию
                            if (!isCommissionSubtracted) {// если комиссия не вычтена из платежей
                                incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                                isCommissionSubtracted = true;
                            }
                            if (!(remainderMap.isEmpty())) {
                                Map.Entry<BigDecimal, BigDecimal> entry = remainderMap.entrySet().iterator().next();

                                BigDecimal incomingPaymentRemainderAmount = entry.getKey();
                                BigDecimal incomingPaymentRateValue = entry.getValue();
                                receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRateValue, outgoingPaymentRate
                                        , incomingPaymentRemainderAmount));
                                incomingPaymentAmount = incomingPaymentAmount.subtract(incomingPaymentRemainderAmount);
                                remainderMap.clear();
                            }
                            if (incomingPaymentAmount.compareTo(outgoingPaymentAmount) > 0) {
                                BigDecimal remainderIncomingPayment = incomingPaymentAmount.subtract(outgoingPaymentAmount);
                                remainderMap.put(remainderIncomingPayment, incomingPaymentRate);
                            }
                        }
                        receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRate, outgoingPaymentRate
                                , outgoingPaymentAmount));
                    }
                }//конец если платежи привысили комиссию
            }
        }
        receivedVSPaidExchangeIncome.setIncome(receivedVSPaidExchangeIncomeAmount);
        if (

                isPositiveOrZero(receivedVSPaidExchangeIncomeAmount)) {
            receivedVSPaidExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_90_7);
        } else {
            receivedVSPaidExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_90_4_60_11);
        }

        return receivedVSPaidExchangeIncome;
    }

    private AccountExchangeIncome buildAccountExchangeIncome() {

        return null;
    }
}







