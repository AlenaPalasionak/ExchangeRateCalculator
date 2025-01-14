package org.example.service;

import org.example.constants.JournalEntryConstants;
import org.example.logger.Log;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.PaymentTransactionEntry;
import org.example.model.non_operating_income.*;
import org.example.util.StringHelper;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.constants.AccountantBookConstants.*;
import static org.example.constants.CurrencyConstants.RUS_RUB;
import static org.example.constants.JournalEntryConstants.ENTRY_52_1_60_11;
import static org.example.constants.JournalEntryConstants.ENTRY_60_11_52_1;

public class RusRubExchangeIncomeService {
    private final ForeignCurrencyAccountantTableService foreignCurrencyAccountantTableService;
    private final ExchangeRateTableService exchangeRateService;
    private final List<List<Object>> transactionTableInForeignCurrency;

    public RusRubExchangeIncomeService() {
        this.foreignCurrencyAccountantTableService = new ForeignCurrencyAccountantTableService();
        List<List<Object>> filterConditions = new ArrayList<>();
        filterConditions.add(Arrays.asList(INCOMING_PAYMENT_AMOUNT, RUS_RUB));
        filterConditions.add(Arrays.asList(INCOMING_PAYMENT_DATE, "\\d+"));
        filterConditions.add(Arrays.asList(OUTGOING_PAYMENT_DATE, "\\d+"));

        this.transactionTableInForeignCurrency = foreignCurrencyAccountantTableService.getFilteredTableByCellContent
                (filterConditions);
        this.exchangeRateService = new ExchangeRateTableService();
    }

    public List<PaymentTransactionEntry> getPaymentTransactionEntry() {
        List<PaymentTransactionEntry> transactionsInForeignCurrency = new ArrayList<>();

        for (List<Object> rowInForeignCurrency : this.transactionTableInForeignCurrency) {
            PaymentTransactionEntry paymentTransactionEntry = createPaymentTransactionEntry(rowInForeignCurrency);
//            if (freightJournalRecord.getCompletionCertificateVSPaymentExchangeIncome() != null && freightJournalRecord
//                    .getCommissionExchangeIncome() != null
//                    && freightJournalRecord.getReceivedVSPaidExchangeIncome() != null && freightJournalRecord
//                    .getAccountExchangeIncome() != null) {
            transactionsInForeignCurrency.add(paymentTransactionEntry);
        }

        return transactionsInForeignCurrency;
    }

    private PaymentTransactionEntry createPaymentTransactionEntry(List<Object> rowObject) {
        BigDecimal receivableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(INCOMING_PAYMENT_AMOUNT)));
        BigDecimal payableAmount = StringHelper.retrieveNumberFromString(String.valueOf(rowObject.get(OUTGOING_PAYMENT_AMOUNT)));

        List<Payment> incomingPayments = foreignCurrencyAccountantTableService.buildIncomingPayment(rowObject);
        List<Payment> outgoingPayments = foreignCurrencyAccountantTableService.buildOutgoingPayment(rowObject);
        BigDecimal incomingPaymentAmountDividedBy100 = incomingPayments.get(INCOMING_PAYMENT_AMOUNT).getPaymentAmount()
                .divide(new BigDecimal("100"));
        BigDecimal incomingPaymentRate = incomingPayments.get(INCOMING_PAYMENT_AMOUNT).getExchangeRate().getRate();
        BigDecimal outgoingPaymentAmountDividedBy100 = outgoingPayments.get(OUTGOING_PAYMENT_AMOUNT).getPaymentAmount()
                .divide(new BigDecimal("100"));
        ;//F - входящий курс оплаты нам
        BigDecimal outgoingPaymentRate = outgoingPayments.get(OUTGOING_PAYMENT_AMOUNT).getExchangeRate().getRate();
        ;//G - курс оплаты перевозчику

        boolean accountantBalance = foreignCurrencyAccountantTableService.isBalance(rowObject);
        String actDate = StringHelper.retrieveDateFromString(String.valueOf(rowObject.get(ACT_DATE)));
        BigDecimal commissionAmountDividedBy100 = foreignCurrencyAccountantTableService.countCommission(rowObject)
                .divide(new BigDecimal("100"));
        ;
        String actNumber = String.valueOf(rowObject.get(ACT_NUMBER));
        ExchangeRate actDateExchangeRate = exchangeRateService.getExchangeRate(actDate);
        BigDecimal actDateExchangeRateAmount = exchangeRateService.getExchangeRateAmount(actDate);
        ExchangeIncomeContainer exchangeIncomeContainer = buildExchangeIncomeContainer(incomingPayments, outgoingPayments
                , receivableAmount, payableAmount, commissionAmountDividedBy100, actDateExchangeRateAmount, actNumber);
        assert exchangeIncomeContainer != null;
        List<AbstractExchangeIncome> actVSIncomingPaymentExchangeIncome = exchangeIncomeContainer.getActVSIncomingPaymentExchangeIncomeList();//I, L
        List<AbstractExchangeIncome> commissionExchangeIncome = exchangeIncomeContainer.getCommissionExchangeIncomeList();//J, M
        List<AbstractExchangeIncome> receivedVSPaidExchangeIncome = exchangeIncomeContainer.getReceivedVSPaidExchangeIncomeList();//K, N
        List<AbstractExchangeIncome> accountExchangeIncome = exchangeIncomeContainer.getAccountExchangeIncomeList(); //O, P

        return new PaymentTransactionEntry(actNumber, receivableAmount, actDateExchangeRate
                , incomingPaymentAmountDividedBy100, incomingPaymentRate, outgoingPaymentAmountDividedBy100
                , outgoingPaymentRate, commissionAmountDividedBy100, actVSIncomingPaymentExchangeIncome, commissionExchangeIncome
                , receivedVSPaidExchangeIncome, accountExchangeIncome, actDate);
    }

    private BigDecimal count(BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        BigDecimal amountDividedBy100 = amount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        Log.info("* * * Class RusRubExchangeIncomeService: method count * * *");
        Log.info("rate1: " + rate1 + ", rate2: " + rate2 + ", amount: " + amount);
        return ((rate1.subtract(rate2)).multiply(amountDividedBy100)).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isAmountFullyPaid(BigDecimal amount, List<Payment> payments) {
        BigDecimal paymentAmountsSum = BigDecimal.ZERO;
        for (Payment payment : payments) {
            paymentAmountsSum = paymentAmountsSum.add(payment.getPaymentAmount());
        }

        return paymentAmountsSum.equals(amount);
    }

    /**
     * new Calculation, general method
     */
    @Nullable
    private ExchangeIncomeContainer buildExchangeIncomeContainer(List<Payment> incomingPayments
            , List<Payment> outgoingPayments, BigDecimal receivableAmount, BigDecimal payableAmount
            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, String actNumber) {

        ExchangeIncomeContainer exchangeIncomeContainer = new ExchangeIncomeContainer();

        List<AbstractExchangeIncome> commissionExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> actVSIncomingPaymentExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> receivedVSPaidExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> accountExchangeIncomeList = new ArrayList<>();

        boolean isPayableAmountFullyPaid = isAmountFullyPaid(payableAmount, outgoingPayments);
        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);

        if (!isPayableAmountFullyPaid && !isReceivableAmountFullyPaid) {
            return null;
        }

        // 1 Create ExchangeIncomeContainer, when ones being paid and ones paid
        else if (incomingPayments.size() == 1 && outgoingPayments.size() == 1) {
            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
            Payment outgoingPayment = outgoingPayments.get(SINGLE_PAYMENT_INDEX);
            BigDecimal outgoingPaymentAmount = outgoingPayment.getPaymentAmount();
            BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , commissionAmount);
            addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
                    JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);

            BigDecimal actVSIncomingPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , payableAmount);
            addExchangeIncome(actVSIncomingPaymentExchangeIncomeList, new ActVSIncomingPaymentExchangeIncome(),
                    actVSIncomingPaymentExchangeIncomeAmount, JournalEntryConstants.ENTRY_62_11_60_11
                    , JournalEntryConstants.ENTRY_60_11_62_11);

            BigDecimal outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
            BigDecimal receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
                    , outgoingPaymentAmount);
            addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
                    receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
                    , JournalEntryConstants.ENTRY_90_4_60_11);

            AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
            if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
                accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
            } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
            accountExchangeIncomeList.add(accountExchangeIncome);

            // 2 Create ExchangeIncomeContainer, when ones being paid and many times paid
        } else if (incomingPayments.size() == 1 && outgoingPayments.size() > 1) {
            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
            BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , commissionAmount);
            addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
                    JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);

            BigDecimal actVSIncomingPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                    , payableAmount);
            addExchangeIncome(actVSIncomingPaymentExchangeIncomeList, new ActVSIncomingPaymentExchangeIncome(),
                    actVSIncomingPaymentExchangeIncomeAmount, JournalEntryConstants.ENTRY_62_11_60_11
                    , JournalEntryConstants.ENTRY_60_11_62_11);

            for (Payment outgoingPayment : outgoingPayments) {
                BigDecimal outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
                BigDecimal outgoingPaymentAmount = outgoingPayment.getPaymentAmount();
                BigDecimal receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
                        , outgoingPaymentAmount);
                addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
                        receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
                        , JournalEntryConstants.ENTRY_90_4_60_11);

                AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
                if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
                    accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
                } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
                accountExchangeIncomeList.add(accountExchangeIncome);
            }
        }

        // 3 Create ExchangeIncomeContainer, when many times being paid and ones paid
        else if (incomingPayments.size() > 1 && outgoingPayments.size() == 1) {
            boolean commissionCovered = false;
            for (Payment incomingPayment : incomingPayments) {
                BigDecimal incomingPaymentRate = incomingPayment.getExchangeRate().getRate();
                BigDecimal incomingPaymentAmount = incomingPayment.getPaymentAmount();
                if (!commissionCovered) {
                    if (incomingPaymentAmount.compareTo(commissionAmount) < 0) {
                        BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                                , incomingPaymentAmount);
                        addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
                                JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);
                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                        commissionAmount = commissionAmount.subtract(incomingPaymentAmount);

                        if (commissionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            commissionCovered = true;
                        }
                    } else if (incomingPaymentAmount.compareTo(commissionAmount) >= 0) {
                        BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                                , commissionAmount);
                        addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
                                JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);
                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
                        commissionCovered = true;
                    }
                }

                if (commissionCovered && incomingPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal actVSIncomingPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
                            , incomingPaymentAmount);//incomingPaymentAmount - правильно, т.к. считаем только то что пришло
                    addExchangeIncome(actVSIncomingPaymentExchangeIncomeList, new ActVSIncomingPaymentExchangeIncome(),
                            actVSIncomingPaymentExchangeIncomeAmount, JournalEntryConstants.ENTRY_62_11_60_11
                            , JournalEntryConstants.ENTRY_60_11_62_11);

                    Payment outgoingPayment = outgoingPayments.get(SINGLE_PAYMENT_INDEX);
                    BigDecimal outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
                    BigDecimal receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
                            , incomingPaymentAmount);//incomingPaymentAmount - правильно, т.к. считаем только то что пришло
                    addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
                            receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
                            , JournalEntryConstants.ENTRY_90_4_60_11);

                    AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
                    if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
                        accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
                    } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
                    accountExchangeIncomeList.add(accountExchangeIncome);
                }
            }
        }

        // 4 Create ExchangeIncomeContainer, when many times being paid and many times paid
//        else if (incomingPayments.size() > 1 && outgoingPayments.size() > 1) {
//            boolean commissionCovered = false;
//            boolean commissionCounted = false;
//            for (Payment incomingPayment : incomingPayments) {
//                BigDecimal incomingPaymentRate = incomingPayment.getExchangeRate().getRate();
//                BigDecimal incomingPaymentAmount = incomingPayment.getPaymentAmount();
//                if (!commissionCovered) {
//                    if (incomingPaymentAmount.compareTo(commissionAmount) < 0) {
//                        BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
//                                , incomingPaymentAmount);
//                        addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
//                                JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);
//                        commissionAmount = commissionAmount.subtract(incomingPaymentAmount);
//                    }
//                    if (commissionAmount.equals(BigDecimal.ZERO)) {
//                        commissionCovered = true;
//                    }
//                } else if (incomingPaymentAmount.compareTo(commissionAmount) >= 0) {
//                    if (!commissionCovered) {
//                        BigDecimal commissionExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
//                                , commissionAmount);
//                        addExchangeIncome(commissionExchangeIncomeList, new CommissionExchangeIncome(), commissionExchangeIncomeAmount,
//                                JournalEntryConstants.ENTRY_62_11_90_7, JournalEntryConstants.ENTRY_90_4_62_11);
//                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
//                        commissionAmount = commissionAmount.subtract(commissionAmount);
//
//                        if (commissionAmount.equals(BigDecimal.ZERO)) {
//                            commissionCovered = true;
//                        }
//                        if (commissionCovered) {
//                            BigDecimal actVSIncomingPaymentExchangeIncomeAmount = count(actDateExchangeRateAmount, incomingPaymentRate
//                                    , incomingPaymentAmount);//incomingPaymentAmount - правильно, т.к. считаем только то что пришло
//                            addExchangeIncome(actVSIncomingPaymentExchangeIncomeList, new ActVSIncomingPaymentExchangeIncome(),
//                                    actVSIncomingPaymentExchangeIncomeAmount, JournalEntryConstants.ENTRY_62_11_60_11
//                                    , JournalEntryConstants.ENTRY_60_11_62_11);
//
//                            Iterator<Payment> iterator = outgoingPayments.iterator();
//                            Payment outgoingPayment = iterator.next();
//
//                            BigDecimal incomingRemainingAmount = BigDecimal.ZERO;
//                            BigDecimal incomingRemainingRate = BigDecimal.ZERO;
//                            BigDecimal outgoingRemainingAmount = BigDecimal.ZERO;
//                            BigDecimal outgoingRemainingRate = BigDecimal.ZERO;
//
//                            BigDecimal outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
//                            BigDecimal outgoingPaymentAmount = outgoingPayment.getPaymentAmount();
//                            BigDecimal receivedVSPaidExchangeIncomeAmount = null;
//                            BigDecimal differenceAmount;
//                            BigDecimal payableAmountCovering = BigDecimal.ZERO;
//                            boolean payableAmountCovered = false;
//                            //payableAmountCovering==payableAmount;
//
//                            differenceAmount = incomingPaymentAmount.subtract(outgoingPaymentAmount);
//                            BigDecimal minAmount;
//                            //***1***   in > out
//                            //    if(!payableAmountCovered) {
//                            if (differenceAmount.compareTo(BigDecimal.ZERO) > 0) {//если входящая больше и будет остаток от нее
//                                minAmount = incomingPaymentAmount.min(outgoingPaymentAmount);
//                                receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
//                                        , minAmount);
//                                addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
//                                        receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
//                                        , JournalEntryConstants.ENTRY_90_4_60_11);
//
//                                AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
//                                if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
//                                    accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
//                                } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
//                                accountExchangeIncomeList.add(accountExchangeIncome);
//
//                                incomingPaymentAmount = differenceAmount;
//                                if (iterator.hasNext()) {
//                                    outgoingPayment = iterator.next();
//                                    outgoingPaymentRate = outgoingPayment.getExchangeRate().getRate();
//                                    outgoingPaymentAmount = outgoingPayment.getPaymentAmount();
//                                    minAmount = incomingPaymentAmount.min(outgoingPaymentAmount);
//                                    receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
//                                            , minAmount);
//                                    addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
//                                            receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
//                                            , JournalEntryConstants.ENTRY_90_4_60_11);
//                                    accountExchangeIncome = new AccountExchangeIncome();
//                                    if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
//                                        accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
//                                    } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
//                                    accountExchangeIncomeList.add(accountExchangeIncome);
//                                    differenceAmount = incomingPaymentAmount.subtract(outgoingPaymentAmount);
//                                }
//                            }
//
//                            //***2***   in < out
//                            else if (differenceAmount.compareTo(BigDecimal.ZERO) < 0) {
//                                receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
//                                        , incomingPaymentAmount);
//                                addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
//                                        receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
//                                        , JournalEntryConstants.ENTRY_90_4_60_11);
//
//                                AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
//                                if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
//                                    accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
//                                } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
//                                accountExchangeIncomeList.add(accountExchangeIncome);
//
//                                outgoingRemainingAmount = differenceAmount.abs();
//                                outgoingRemainingRate = outgoingPaymentRate;
//                                //***3***    in = out
//                            } else if (differenceAmount.compareTo(BigDecimal.ZERO) == 0) {
//                                receivedVSPaidExchangeIncomeAmount = count(incomingPaymentRate, outgoingPaymentRate
//                                        , outgoingPaymentAmount);
//                                addExchangeIncome(receivedVSPaidExchangeIncomeList, new ReceivedVSPaidExchangeIncome(),
//                                        receivedVSPaidExchangeIncomeAmount, JournalEntryConstants.ENTRY_60_11_90_7
//                                        , JournalEntryConstants.ENTRY_90_4_60_11);
//
//                                AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
//                                if (receivedVSPaidExchangeIncomeAmount.compareTo(BigDecimal.ZERO) > 0) {
//                                    accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);
//                                } else accountExchangeIncome.setJournalEntry(ENTRY_60_11_52_1);
//                                accountExchangeIncomeList.add(accountExchangeIncome);
//                            }
//                        }
//                    }
//                }
//            }
//        }

// add first object into container - actVSIncomingPaymentExchangeIncomeList
        exchangeIncomeContainer.

                setCommissionExchangeIncomeList(commissionExchangeIncomeList);
// add second object into container - actVSIncomingPaymentExchangeIncomeList
        exchangeIncomeContainer.

                setActVSIncomingPaymentExchangeIncomeList(actVSIncomingPaymentExchangeIncomeList);
// add third object into container - receivedVSPaidExchangeIncomeList
        exchangeIncomeContainer.

                setReceivedVSPaidExchangeIncomeList(receivedVSPaidExchangeIncomeList);
// add fourth object into container - accountExchangeIncomeList
        exchangeIncomeContainer.

                setAccountExchangeIncomeList(accountExchangeIncomeList);

        return exchangeIncomeContainer;
    }

    private void addExchangeIncome
            (List<AbstractExchangeIncome> incomeList, AbstractExchangeIncome income,
             BigDecimal incomeAmount, String positiveEntry, String negativeEntry) {
        income.setIncomeAmount(incomeAmount);
        if (isPositiveOrZero(incomeAmount)) {
            income.setJournalEntry(positiveEntry);
        } else {
            income.setJournalEntry(negativeEntry);
        }
        incomeList.add(income);
    }

//    private CommissionExchangeIncome buildCommissionExchangeIncome(List<Payment> incomingPayments
//            , BigDecimal commissionAmount, BigDecimal actDateExchangeRateAmount, BigDecimal receivableAmount) {
//        CommissionExchangeIncome commissionIncome = new CommissionExchangeIncome();
//
//        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);
//        BigDecimal accumulatedPayments = BigDecimal.ZERO;
//        BigDecimal commissionExchangeIncomeAmount = BigDecimal.ZERO;
//
//        if (incomingPayments.size() > 1 & isReceivableAmountFullyPaid) {
//
//            for (Payment payment : incomingPayments) {
//                BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
//                accumulatedPayments = accumulatedPayments.add(incomingPaymentAmount);
//                if (accumulatedPayments.compareTo(commissionAmount) <= 0) {
//                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
//                    commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(count(actDateExchangeRateAmount, incomingPaymentRate
//                            , incomingPaymentAmount));
//                }
//            }
//        } else { //конец если меньше чем комиссия заплатили
//            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
//            Log.info("* * * Class RusRubExchangeIncomeService * * * ");
//            Log.info("actDateExchangeRateAmount " + actDateExchangeRateAmount);
//            Log.info("commissionAmount " + commissionAmount);
//            Log.info("incomingPaymentRate " + incomingPaymentRate);
//
//            commissionExchangeIncomeAmount = commissionExchangeIncomeAmount.add(count(actDateExchangeRateAmount
//                    , incomingPaymentRate, commissionAmount));
//            Log.info("commissionExchangeIncomeAmount " + commissionExchangeIncomeAmount);
//        }
//
//        commissionIncome.setIncomeAmount(commissionExchangeIncomeAmount);
//        if (isPositiveOrZero(commissionExchangeIncomeAmount)) {
//            commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_62_11_90_7);
//        } else {
//            commissionIncome.setJournalEntry(JournalEntryConstants.ENTRY_90_4_62_11);
//        }
//
//        return commissionIncome;
//    }

//    private ReceivedVSPaidExchangeIncome buildReceivedVSPaidExchangeIncome(List<Payment> incomingPayments
//            , List<Payment> outgoingPayments, BigDecimal receivableAmount
//            , BigDecimal payableAmount, BigDecimal commissionAmount) {
//
//        ReceivedVSPaidExchangeIncome receivedVSPaidExchangeIncome = null;
//
//        boolean isPayableAmountFullyPaid = isAmountFullyPaid(payableAmount, outgoingPayments);
//        boolean isReceivableAmountFullyPaid = isAmountFullyPaid(receivableAmount, incomingPayments);
//
//        BigDecimal receivedVSPaidExchangeIncomeAmount = BigDecimal.ZERO;
//
//        BigDecimal accumulatedIncomingPayments = BigDecimal.ZERO;
//        boolean isCommissionSubtracted = false;
//
//        if (!isPayableAmountFullyPaid && !isReceivableAmountFullyPaid) {
//            JOptionPane.showMessageDialog(null, "Входящий или исходящий долг не полностью погашен");
//        } else if (incomingPayments.size() > 1 && outgoingPayments.size() == 1) {
//            BigDecimal outgoingPaymentRate = outgoingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
//            for (Payment payment : incomingPayments) {//пойдем по всем платежам
//                BigDecimal incomingPaymentAmount = payment.getPaymentAmount();
//                accumulatedIncomingPayments = accumulatedIncomingPayments.add(incomingPaymentAmount);//складываем всеплатежи тут
//                if (accumulatedIncomingPayments.compareTo(commissionAmount) > 0) {// если платежи привысили комиссию
//                    if (!isCommissionSubtracted) {// если комиссия не вычтена из платежей
//                        incomingPaymentAmount = incomingPaymentAmount.subtract(commissionAmount);
//                        isCommissionSubtracted = true;
//                    }//коммиссия вычтена
//                    BigDecimal incomingPaymentRate = payment.getExchangeRate().getRate();
//                    receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRate, outgoingPaymentRate
//                            , incomingPaymentAmount));
//                }
//            }
//        } else if (incomingPayments.size() == 1 && outgoingPayments.size() == 1) {
//            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
//            BigDecimal outgoingPaymentRate = outgoingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
//            receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add(count(incomingPaymentRate
//                    , outgoingPaymentRate, payableAmount));
//        } else if (incomingPayments.size() == 1 && outgoingPayments.size() > 1) {
//            BigDecimal incomingPaymentRate = incomingPayments.get(SINGLE_PAYMENT_INDEX).getExchangeRate().getRate();
//            for (Payment payment : outgoingPayments) {//пойдем по всем платежам
//                BigDecimal outgoingPaymentAmount = payment.getPaymentAmount();
//                BigDecimal outgoingPaymentRate = payment.getExchangeRate().getRate();
//                receivedVSPaidExchangeIncomeAmount = receivedVSPaidExchangeIncomeAmount.add
//                        (count(incomingPaymentRate, outgoingPaymentRate, outgoingPaymentAmount));
//            }
//        }
//        if (!receivedVSPaidExchangeIncomeAmount.equals(BigDecimal.ZERO)) {
//            receivedVSPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();
//            receivedVSPaidExchangeIncome.setIncomeAmount(receivedVSPaidExchangeIncomeAmount);
//            if (isPositiveOrZero(receivedVSPaidExchangeIncomeAmount)) {
//                receivedVSPaidExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_60_11_90_7);
//            } else {
//                receivedVSPaidExchangeIncome.setJournalEntry(JournalEntryConstants.ENTRY_90_4_60_11);
//            }
//        }
//
//        return receivedVSPaidExchangeIncome;
//    }

    private String buildAccountExchangeIncomeJournalEntry(ReceivedVSPaidExchangeIncome
                                                                  receivedPaidExchangeIncome) {
        if (receivedPaidExchangeIncome.getIncomeAmount().compareTo(BigDecimal.ZERO) > 0) {
            return (ENTRY_52_1_60_11);
        } else return ENTRY_60_11_52_1;
    }

    private boolean isPositiveOrZero(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) >= 0;
    }
}







