package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.PaymentTransactionEntry;
import org.example.model.non_operating_income.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.JournalEntryConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RusRubExchangeIncomeServiceTest {
    PaymentTransactionEntry paymentTransactionEntry;

    @BeforeEach
    public void createPaymentTransactionEntry() {
        paymentTransactionEntry = new PaymentTransactionEntry();

        String actNumber = "2";//A +
        BigDecimal actAmount = new BigDecimal("72000");//B
        String actDate = " 21.01.2024";//Q

        AbstractExchangeIncome actVSIncomingPaymentExchangeIncome = new ActVSIncomingPaymentExchangeIncome();
        BigDecimal actInExchangeIncomeAmount = new BigDecimal("14,69");
        BigDecimal actInRate1 = new BigDecimal("3.5741");
        BigDecimal actInRate2 = new BigDecimal("3.5537");
        BigDecimal actInPaymentAmount = new BigDecimal("720");
        actVSIncomingPaymentExchangeIncome.setExchangeIncomeAmount(actInExchangeIncomeAmount);
        actVSIncomingPaymentExchangeIncome.setJournalEntry(ENTRY_62_11_60_11);
        actVSIncomingPaymentExchangeIncome.setRate1(actInRate1);
        actVSIncomingPaymentExchangeIncome.setRate1(actInRate2);
        actVSIncomingPaymentExchangeIncome.setPaymentAmount(actInPaymentAmount);

        AbstractExchangeIncome commissionExchangeIncome = new CommissionExchangeIncome();
        BigDecimal commissionExchangeIncomeAmount = new BigDecimal("1.02");
        BigDecimal commissionRate1 = new BigDecimal("3.5741");
        BigDecimal commissionRate2 = new BigDecimal("3.5537");
        BigDecimal commissionPaymentAmount = new BigDecimal("50");
        commissionExchangeIncome.setExchangeIncomeAmount(commissionExchangeIncomeAmount);
        commissionExchangeIncome.setJournalEntry(ENTRY_62_11_90_7);
        commissionExchangeIncome.setRate1(commissionRate1);
        commissionExchangeIncome.setRate1(commissionRate2);
        commissionExchangeIncome.setPaymentAmount(commissionPaymentAmount);

        AbstractExchangeIncome receivedVSPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();
        BigDecimal receivedPaidExchangeIncomeAmount = new BigDecimal("11.66");
        BigDecimal receivedPaidRate1 = new BigDecimal("3.5537");
        BigDecimal receivedPaidRate2 = new BigDecimal("3.5363");
        BigDecimal receivedPaidPaymentAmount = new BigDecimal("670");
        receivedVSPaidExchangeIncome.setExchangeIncomeAmount(receivedPaidExchangeIncomeAmount);
        receivedVSPaidExchangeIncome.setJournalEntry(ENTRY_60_11_90_7);
        receivedVSPaidExchangeIncome.setRate1(receivedPaidRate1);
        receivedVSPaidExchangeIncome.setRate1(receivedPaidRate2);
        receivedVSPaidExchangeIncome.setPaymentAmount(receivedPaidPaymentAmount);

        AbstractExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
        receivedVSPaidExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);

        paymentTransactionEntry.setActNumber(actNumber);
        paymentTransactionEntry.setActAmount(actAmount);
        paymentTransactionEntry.setActDate(actDate);
        paymentTransactionEntry.setActVSIncomingPaymentExchangeIncome(actVSIncomingPaymentExchangeIncome);
        paymentTransactionEntry.setCommissionExchangeIncome(commissionExchangeIncome);
        paymentTransactionEntry.setReceivedVSPaidExchangeIncome(receivedVSPaidExchangeIncome);
        paymentTransactionEntry.setAccountExchangeIncome(accountExchangeIncome);




        expectedPaymentTransactionEntry = new PaymentTransactionEntry(receivableAmount, payableAmount, incomingPayments, outgoingPayments
                , false, "21.01.2024", new BigDecimal("5000")
                , "2", actDateExchangeRate, actVSIncomingPaymentExchangeIncome
                , commissionExchangeIncome, receivedPaidExchangeIncome, accountExchangeIncome);
    }

    @Test
    void getPaymentTransactionEntryList() {

        List<PaymentTransactionEntry> sourceTableData = new RusRubExchangeIncomeService().getPaymentTransactionEntryList();

        PaymentTransactionEntry actualPaymentTransactionEntry = sourceTableData.get(0);

        createPaymentTransactionEntry();

        assertEquals(expectedPaymentTransactionEntry.getIncomingPaymentList(), actualPaymentTransactionEntry.getIncomingPaymentList());
        assertEquals(expectedPaymentTransactionEntry.getOutgoingPaymentList(), actualPaymentTransactionEntry.getOutgoingPaymentList());

        assertEquals(expectedPaymentTransactionEntry.isAccountBalance()
                , actualPaymentTransactionEntry.isAccountBalance());

        assertEquals(expectedPaymentTransactionEntry.getActDate()
                , actualPaymentTransactionEntry.getActDate());

        assertEquals(expectedPaymentTransactionEntry.getActNumber()
                , actualPaymentTransactionEntry.getActNumber());

        assertEquals(expectedPaymentTransactionEntry.getCommission(), actualPaymentTransactionEntry.getCommission());
        assertEquals(expectedPaymentTransactionEntry.getActDateExchangeRate(), actualPaymentTransactionEntry.getActDateExchangeRate());

        assertEquals(expectedPaymentTransactionEntry.getCompletionCertificateVSPaymentExchangeIncome()
                , actualPaymentTransactionEntry.getCompletionCertificateVSPaymentExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getCommissionExchangeIncome()
                , actualPaymentTransactionEntry.getCommissionExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getReceivedVSPaidExchangeIncome()
                , actualPaymentTransactionEntry.getReceivedVSPaidExchangeIncome());

        assertEquals(expectedPaymentTransactionEntry.getAccountExchangeIncome().getExchangeIncomeAmount()
                , actualPaymentTransactionEntry.getAccountExchangeIncome().getExchangeIncomeAmount());

        assertEquals(expectedPaymentTransactionEntry.getAccountExchangeIncome().getJournalEntry()
                , actualPaymentTransactionEntry.getAccountExchangeIncome().getJournalEntry());

        assertEquals(expectedPaymentTransactionEntry.getReceivableAmount()
                , actualPaymentTransactionEntry.getReceivableAmount());

        assertEquals(expectedPaymentTransactionEntry.getPayableAmount()
                , actualPaymentTransactionEntry.getPayableAmount());

        assertEquals(expectedPaymentTransactionEntry, actualPaymentTransactionEntry);
    }
}
