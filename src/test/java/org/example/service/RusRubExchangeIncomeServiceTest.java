package org.example.service;

import org.example.model.PaymentTransactionEntry;
import org.example.model.non_operating_income.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.example.constants.JournalEntryConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RusRubExchangeIncomeServiceTest {
    PaymentTransactionEntry expectedPaymentTransactionEntry;

    @BeforeEach
    public void createPaymentTransactionEntry() {
        expectedPaymentTransactionEntry = new PaymentTransactionEntry();

        String actNumber = "2";//A +
        BigDecimal actAmount = new BigDecimal("72000");//B
        String actDate = "21.01.2024";//Q

        AbstractExchangeIncome actVSIncomingPaymentExchangeIncome = new ActVSIncomingPaymentExchangeIncome();
        BigDecimal actInExchangeIncomeAmount = new BigDecimal("13.67");
        BigDecimal actInRate1 = new BigDecimal("3.5741");
        BigDecimal actInRate2 = new BigDecimal("3.5537");
        BigDecimal actInPaymentAmount = new BigDecimal("670.0");
        actVSIncomingPaymentExchangeIncome.setExchangeIncomeAmount(actInExchangeIncomeAmount);
        actVSIncomingPaymentExchangeIncome.setJournalEntry(ENTRY_62_11_60_11);
        actVSIncomingPaymentExchangeIncome.setRate1(actInRate1);
        actVSIncomingPaymentExchangeIncome.setRate1(actInRate2);
        actVSIncomingPaymentExchangeIncome.setPaymentAmount(actInPaymentAmount);

        AbstractExchangeIncome commissionExchangeIncome = new CommissionExchangeIncome();
        BigDecimal commissionExchangeIncomeAmount = new BigDecimal("1.02");
        BigDecimal commissionRate1 = new BigDecimal("3.5741");
        BigDecimal commissionRate2 = new BigDecimal("3.5537");
        BigDecimal commissionPaymentAmount = new BigDecimal("50.0");
        commissionExchangeIncome.setExchangeIncomeAmount(commissionExchangeIncomeAmount);
        commissionExchangeIncome.setJournalEntry(ENTRY_62_11_90_7);
        commissionExchangeIncome.setRate1(commissionRate1);
        commissionExchangeIncome.setRate1(commissionRate2);
        commissionExchangeIncome.setPaymentAmount(commissionPaymentAmount);

        AbstractExchangeIncome receivedVSPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();
        BigDecimal receivedPaidExchangeIncomeAmount = new BigDecimal("11.66");
        BigDecimal receivedPaidRate1 = new BigDecimal("3.5537");
        BigDecimal receivedPaidRate2 = new BigDecimal("3.5363");
        BigDecimal receivedPaidPaymentAmount = new BigDecimal("670.0");
        receivedVSPaidExchangeIncome.setExchangeIncomeAmount(receivedPaidExchangeIncomeAmount);
        receivedVSPaidExchangeIncome.setJournalEntry(ENTRY_60_11_90_7);
        receivedVSPaidExchangeIncome.setRate1(receivedPaidRate1);
        receivedVSPaidExchangeIncome.setRate1(receivedPaidRate2);
        receivedVSPaidExchangeIncome.setPaymentAmount(receivedPaidPaymentAmount);

        AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
        accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);

        expectedPaymentTransactionEntry.setActNumber(actNumber);
        expectedPaymentTransactionEntry.setActAmount(actAmount);
        expectedPaymentTransactionEntry.setActDate(actDate);
        expectedPaymentTransactionEntry.setActVSIncomingPaymentExchangeIncome(actVSIncomingPaymentExchangeIncome);
        expectedPaymentTransactionEntry.setCommissionExchangeIncome(commissionExchangeIncome);
        expectedPaymentTransactionEntry.setReceivedVSPaidExchangeIncome(receivedVSPaidExchangeIncome);
        expectedPaymentTransactionEntry.setAccountExchangeIncome(accountExchangeIncome);
    }

    @Test
    void getPaymentTransactionEntryList() {

        List<PaymentTransactionEntry> paymentTransactionEntryList = new RusRubExchangeIncomeService()
                .getPaymentTransactionEntryList();

        PaymentTransactionEntry actualPaymentTransactionEntry = paymentTransactionEntryList.get(0);

        createPaymentTransactionEntry();

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
