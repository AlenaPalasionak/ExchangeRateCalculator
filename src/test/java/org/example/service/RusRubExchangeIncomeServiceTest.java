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
    PaymentTransactionEntry expectedPaymentTransactionEntry;

    @BeforeEach
    public void createPaymentTransactionEntry() {
        ExchangeIncomeContainerForOneFreight exchangeIncomeContainerForOneFreight = new ExchangeIncomeContainerForOneFreight();

        List<AbstractExchangeIncome> commissionExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> actVSIncomingPaymentExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> receivedVSPaidExchangeIncomeList = new ArrayList<>();
        List<AbstractExchangeIncome> accountExchangeIncomeList = new ArrayList<>();

        AbstractExchangeIncome completionCertificateVSPaymentExchangeIncome;
        AbstractExchangeIncome commissionExchangeIncome;
        AbstractExchangeIncome receivedVSPaidExchangeIncome;
        AbstractExchangeIncome accountExchange;

        String actNumber = "2";//A +
        BigDecimal actAmount = new BigDecimal("72000");//B
        ExchangeRate actDateExchangeRate = new BigDecimal("3.5741");;//C
        BigDecimal incomingPaymentAmountDividedBy100;// D
        BigDecimal incomingPaymentRate;//E
        BigDecimal outgoingPaymentAmountDividedBy100;//F - входящий курс оплаты нам
        BigDecimal outgoingPaymentRate;//G - курс оплаты перевозчику
        BigDecimal commissionDividedBy100;// H

        List<AbstractExchangeIncome> actVSIncomingPaymentExchangeIncome;//I, L
        List<AbstractExchangeIncome> commissionExchangeIncome;//J, M
        List<AbstractExchangeIncome> receivedVSPaidExchangeIncome;//K, N
        List<AbstractExchangeIncome> accountExchangeIncome;//O, P
        String actDate;//Q

        Payment incomingPayment = new Payment(new BigDecimal("72000")
                , "01.02.2024"
                , new ExchangeRate("01.02.2024", new BigDecimal("3.5537"))
                , "рос");
        incomingPayments.add(incomingPayment);
        Payment ougoingPayment = new Payment(new BigDecimal("67000")
                , "10.02.2024"
                , new ExchangeRate("10.02.2024", new BigDecimal("3.5363"))
                , "рос");
        outgoingPayments.add(ougoingPayment);
        BigDecimal actDateExchangeRateAmount =
        ExchangeRate actDateExchangeRate = new ExchangeRate("21.01.2024", actDateExchangeRateAmount);
        BigDecimal receivableAmount = new BigDecimal("72000");
        BigDecimal payableAmount = new BigDecimal("67000");

        ActVSIncomingPaymentExchangeIncome actVSIncomingPaymentExchangeIncome
                = new ActVSIncomingPaymentExchangeIncome();
        actVSIncomingPaymentExchangeIncome.setIncomeAmount(new BigDecimal("13.67"));
        actVSIncomingPaymentExchangeIncome.setJournalEntry(ENTRY_62_11_60_11);

        CommissionExchangeIncome commissionExchangeIncome = new CommissionExchangeIncome();
        commissionExchangeIncome.setIncomeAmount(new BigDecimal("1.02"));
        commissionExchangeIncome.setJournalEntry(ENTRY_62_11_90_7);

        ReceivedVSPaidExchangeIncome receivedPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();
        receivedPaidExchangeIncome.setIncomeAmount(new BigDecimal("11.66"));
        receivedPaidExchangeIncome.setJournalEntry(ENTRY_60_11_90_7);

        AccountExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
        accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);

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

        assertEquals(expectedPaymentTransactionEntry.getAccountExchangeIncome().getIncomeAmount()
                , actualPaymentTransactionEntry.getAccountExchangeIncome().getIncomeAmount());

        assertEquals(expectedPaymentTransactionEntry.getAccountExchangeIncome().getJournalEntry()
                , actualPaymentTransactionEntry.getAccountExchangeIncome().getJournalEntry());

        assertEquals(expectedPaymentTransactionEntry.getReceivableAmount()
                , actualPaymentTransactionEntry.getReceivableAmount());

        assertEquals(expectedPaymentTransactionEntry.getPayableAmount()
                , actualPaymentTransactionEntry.getPayableAmount());

        assertEquals(expectedPaymentTransactionEntry, actualPaymentTransactionEntry);
    }
}
