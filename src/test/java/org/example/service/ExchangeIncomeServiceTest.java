package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AccountExchangeIncome;
import org.example.model.non_operating_income.CommissionExchangeIncome;
import org.example.model.non_operating_income.CompletionCertificateVSPaymentExchangeIncome;
import org.example.model.non_operating_income.ReceivedVSPaidExchangeIncome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.JournalEntryConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeIncomeServiceTest {
    Transaction expectedTransaction;

    @BeforeEach
    public void createTransaction() {
        List<Payment> incomingPayments = new ArrayList<>();
        List<Payment> outgoingPayments = new ArrayList<>();

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
        BigDecimal actDateExchangeRateAmount = new BigDecimal("3.5741");
        ExchangeRate actDateExchangeRate = new ExchangeRate("21.01.2024", actDateExchangeRateAmount);
        BigDecimal receivableAmount = new BigDecimal("72000");
        BigDecimal payableAmount = new BigDecimal("67000");

        CompletionCertificateVSPaymentExchangeIncome completionCertificateVSPaymentExchangeIncome
                = new CompletionCertificateVSPaymentExchangeIncome();
        completionCertificateVSPaymentExchangeIncome.setIncomeAmount(new BigDecimal("13.67"));
        completionCertificateVSPaymentExchangeIncome.setJournalEntry(ENTRY_62_11_60_11);

        CommissionExchangeIncome commissionExchangeIncome = new CommissionExchangeIncome();
        commissionExchangeIncome.setIncomeAmount(new BigDecimal("1.02"));
        commissionExchangeIncome.setJournalEntry(ENTRY_62_11_90_7);

        ReceivedVSPaidExchangeIncome receivedPaidExchangeIncome = new ReceivedVSPaidExchangeIncome();
        receivedPaidExchangeIncome.setIncomeAmount(new BigDecimal("11.66"));
        receivedPaidExchangeIncome.setJournalEntry(ENTRY_60_11_90_7);

        AccountExchangeIncome accountExchangeIncome = new AccountExchangeIncome();
        accountExchangeIncome.setJournalEntry(ENTRY_52_1_60_11);

        expectedTransaction = new Transaction(receivableAmount, payableAmount, incomingPayments, outgoingPayments
                , false, "21.01.2024", new BigDecimal("5000")
                , "2", actDateExchangeRate, completionCertificateVSPaymentExchangeIncome
                , commissionExchangeIncome, receivedPaidExchangeIncome, accountExchangeIncome);
    }

    @Test
    void getTransactions() {
        List<Transaction> transactions = new RusRubExchangeIncomeService().getTransactions();

        Transaction actualTransaction = transactions.get(0);

        createTransaction();

        assertEquals(expectedTransaction.getIncomingPaymentList(), actualTransaction.getIncomingPaymentList());
        assertEquals(expectedTransaction.getOutgoingPaymentList(), actualTransaction.getOutgoingPaymentList());

        assertEquals(expectedTransaction.isAccountBalance()
                , actualTransaction.isAccountBalance());

        assertEquals(expectedTransaction.getActDate()
                , actualTransaction.getActDate());

        assertEquals(expectedTransaction.getActNumber()
                , actualTransaction.getActNumber());

        assertEquals(expectedTransaction.getCommission(), actualTransaction.getCommission());
        assertEquals(expectedTransaction.getActDateExchangeRate(), actualTransaction.getActDateExchangeRate());

        assertEquals(expectedTransaction.getCompletionCertificateVSPaymentExchangeIncome()
                , actualTransaction.getCompletionCertificateVSPaymentExchangeIncome());

        assertEquals(expectedTransaction.getCommissionExchangeIncome()
                , actualTransaction.getCommissionExchangeIncome());

        assertEquals(expectedTransaction.getReceivedVSPaidExchangeIncome()
                , actualTransaction.getReceivedVSPaidExchangeIncome());

        assertEquals(expectedTransaction.getAccountExchangeIncome().getIncomeAmount()
                , actualTransaction.getAccountExchangeIncome().getIncomeAmount());

        assertEquals(expectedTransaction.getAccountExchangeIncome().getJournalEntry()
                , actualTransaction.getAccountExchangeIncome().getJournalEntry());

        assertEquals(expectedTransaction.getReceivableAmount()
                , actualTransaction.getReceivableAmount());

        assertEquals(expectedTransaction.getPayableAmount()
                , actualTransaction.getPayableAmount());

        assertEquals(expectedTransaction, actualTransaction);
    }
}
