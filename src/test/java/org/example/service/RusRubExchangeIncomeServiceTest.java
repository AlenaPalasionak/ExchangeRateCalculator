package org.example.service;

import org.example.model.PaymentTransactionEntry;
import org.example.model.non_operating_income.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.JournalEntryConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RusRubExchangeIncomeServiceTest {

    static AbstractExchangeIncome createExchangeIncome(AbstractExchangeIncome paymentExchangeIncome
            , BigDecimal exchangeIncomeAmount, String journalEntry, BigDecimal rate1, BigDecimal rate2
            , BigDecimal paymentAmount) {
        paymentExchangeIncome.setExchangeIncomeAmount(exchangeIncomeAmount);
        paymentExchangeIncome.setJournalEntry(journalEntry);
        paymentExchangeIncome.setRate1(rate1);
        paymentExchangeIncome.setRate1(rate2);
        paymentExchangeIncome.setPaymentAmount(paymentAmount);

        return paymentExchangeIncome;
    }

    static PaymentTransactionEntry createExpectedPaymentTransactionEntry(String actNumber, BigDecimal actAmount, String actDate
            , AbstractExchangeIncome actVSIncomingPaymentExchangeIncome
            , AbstractExchangeIncome commissionExchangeIncome, AbstractExchangeIncome receivedVSPaidExchangeIncome
            , AbstractExchangeIncome accountExchangeIncome) {
        PaymentTransactionEntry expectedPaymentTransactionEntry = new PaymentTransactionEntry();
        expectedPaymentTransactionEntry.setActNumber(actNumber);
        expectedPaymentTransactionEntry.setActAmount(actAmount);
        expectedPaymentTransactionEntry.setActDate(actDate);
        expectedPaymentTransactionEntry.setActVSIncomingPaymentExchangeIncome(actVSIncomingPaymentExchangeIncome);
        expectedPaymentTransactionEntry.setCommissionExchangeIncome(commissionExchangeIncome);
        expectedPaymentTransactionEntry.setReceivedVSPaidExchangeIncome(receivedVSPaidExchangeIncome);
        expectedPaymentTransactionEntry.setAccountExchangeIncome(accountExchangeIncome);
        return expectedPaymentTransactionEntry;
    }

    // сделать метод возвращающий AbstractExchangeIncome принимающий нужные параметры
    static List<PaymentTransactionEntry> provideExpectedPaymentTransactionEntryList() {
        List<PaymentTransactionEntry> paymentTransactionEntryList = new ArrayList<>();

        paymentTransactionEntryList.add(createExpectedPaymentTransactionEntry(
                "2", new BigDecimal("72000"), "21.01.2024",
                createExchangeIncome(new ActVSIncomingPaymentExchangeIncome(),
                        new BigDecimal("13.67"), ENTRY_62_11_60_11,
                        new BigDecimal("3.5741"), new BigDecimal("3.5537"),
                        new BigDecimal("670.0")),
                createExchangeIncome(new CommissionExchangeIncome(),
                        new BigDecimal("1.02"), ENTRY_62_11_90_7,
                        new BigDecimal("3.5741"), new BigDecimal("3.5537"),
                        new BigDecimal("50.0")),
                createExchangeIncome(new ReceivedVSPaidExchangeIncome(),
                        new BigDecimal("11.66"), ENTRY_60_11_90_7,
                        new BigDecimal("3.5537"), new BigDecimal("3.5363"),
                        new BigDecimal("670.0")),
                createExchangeIncome(new AccountExchangeIncome(),
                        null, ENTRY_52_1_60_11, null, null, null)
        ));
        paymentTransactionEntryList.add(createExpectedPaymentTransactionEntry(
                "2", new BigDecimal("72000"), "21.01.2024",
                createExchangeIncome(new ActVSIncomingPaymentExchangeIncome(),
                        new BigDecimal("13.67"), ENTRY_62_11_60_11,
                        new BigDecimal("3.5741"), new BigDecimal("3.5537"),
                        new BigDecimal("670.0")),
                createExchangeIncome(new CommissionExchangeIncome(),
                        new BigDecimal("1.02"), ENTRY_62_11_90_7,
                        new BigDecimal("3.5741"), new BigDecimal("3.5537"),
                        new BigDecimal("50.0")),
                createExchangeIncome(new ReceivedVSPaidExchangeIncome(),
                        new BigDecimal("11.66"), ENTRY_60_11_90_7,
                        new BigDecimal("3.5537"), new BigDecimal("3.5363"),
                        new BigDecimal("670.0")),
                createExchangeIncome(new AccountExchangeIncome(),
                        null, ENTRY_52_1_60_11, null, null, null)
        ));

        paymentTransactionEntryList.add(createExpectedPaymentTransactionEntry(
                "3", new BigDecimal("80000"), "18.01.2024",
                createExchangeIncome(new ActVSIncomingPaymentExchangeIncome(),
                        new BigDecimal("31.01"), ENTRY_62_11_60_11,
                        new BigDecimal("3.5801"), new BigDecimal("3.5382"),
                        new BigDecimal("740.0")),
                createExchangeIncome(new CommissionExchangeIncome(),
                        new BigDecimal("2.51"), ENTRY_62_11_90_7,
                        new BigDecimal("3.5801"), new BigDecimal("3.5382"),
                        new BigDecimal("60.0")),
                createExchangeIncome(new ReceivedVSPaidExchangeIncome(),
                        new BigDecimal("0.10"), ENTRY_60_11_90_7,
                        new BigDecimal("3.5382"), new BigDecimal("3.5363"),
                        new BigDecimal("50.0")),
                createExchangeIncome(new AccountExchangeIncome(),
                        null, ENTRY_52_1_60_11, null, null, null)
        ));

        return paymentTransactionEntryList;
    }

    static List<Integer> provideIndices() {
        List<Integer> indices = new ArrayList<>();
        indices.add(0);
        indices.add(1);
//        indices.add(2);
//        indices.add(3);
//        indices.add(4);
//        indices.add(5);

        return indices;
    }

    @ParameterizedTest
    @MethodSource("provideIndices")
    void getPaymentTransactionEntryList(int index) {
        List<PaymentTransactionEntry> paymentTransactionEntryList = new RusRubExchangeIncomeService()
                .getPaymentTransactionEntryList();
        PaymentTransactionEntry actualPaymentTransactionEntry = null;
        PaymentTransactionEntry expectedPaymentTransactionEntry = null;
        if (index == 0) {
            actualPaymentTransactionEntry = paymentTransactionEntryList.get(index);
            expectedPaymentTransactionEntry = provideExpectedPaymentTransactionEntryList().get(index);
        } else if (index == 1) {
            actualPaymentTransactionEntry = paymentTransactionEntryList.get(index);
            expectedPaymentTransactionEntry = provideExpectedPaymentTransactionEntryList().get(index);
        }
        //else if (index == 2) {
//            actualPaymentTransactionEntry = paymentTransactionEntryList.get(1);
//        } else if (index == 3) {
//            actualPaymentTransactionEntry = paymentTransactionEntryList.get(1);
//        } else if (index == 4) {
//            actualPaymentTransactionEntry = paymentTransactionEntryList.get(1);
//        } else if (index == 5) {
//            actualPaymentTransactionEntry = paymentTransactionEntryList.get(1);
//        }

        assert actualPaymentTransactionEntry != null && expectedPaymentTransactionEntry != null;
        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getPaymentAmount()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getPaymentAmount());
        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getRate1()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getRate1());
        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getRate2()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getRate2());
        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getJournalEntry()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getJournalEntry());
        assertEquals(expectedPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getExchangeIncomeAmount()
                , actualPaymentTransactionEntry.getActVSIncomingPaymentExchangeIncome()
                        .getExchangeIncomeAmount());

        assertEquals(expectedPaymentTransactionEntry.getCommissionExchangeIncome()
                , actualPaymentTransactionEntry.getCommissionExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome());
        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getRate1()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getRate1());
        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getRate2()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getRate2());
        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getPaymentAmount()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getPaymentAmount());
        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getJournalEntry()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getJournalEntry());
        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getExchangeIncomeAmount()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome().getExchangeIncomeAmount());

        assertEquals(expectedPaymentTransactionEntry.getAccountExchangeIncome()
                , actualPaymentTransactionEntry.getAccountExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getActDate()
                , actualPaymentTransactionEntry.getActDate());

        assertEquals(expectedPaymentTransactionEntry.getActNumber()
                , actualPaymentTransactionEntry.getActNumber());

        assertEquals(expectedPaymentTransactionEntry.getActAmount()
                , actualPaymentTransactionEntry.getActAmount());

        assertEquals(expectedPaymentTransactionEntry, actualPaymentTransactionEntry);
    }
}
