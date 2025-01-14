package org.example.service;

import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.FreightJournalRecord;
import org.example.model.non_operating_income.AccountExchangeIncome;
import org.example.model.non_operating_income.CommissionExchangeIncome;
import org.example.model.non_operating_income.ActVSIncomingPaymentExchangeIncome;
import org.example.model.non_operating_income.ReceivedVSPaidExchangeIncome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.constants.JournalEntryConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RusRubExchangeIncomeServiceTest {
    FreightJournalRecord expectedFreightJournalRecord;

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

        expectedFreightJournalRecord = new FreightJournalRecord(receivableAmount, payableAmount, incomingPayments, outgoingPayments
                , false, "21.01.2024", new BigDecimal("5000")
                , "2", actDateExchangeRate, actVSIncomingPaymentExchangeIncome
                , commissionExchangeIncome, receivedPaidExchangeIncome, accountExchangeIncome);
    }

    @Test
    void getTransactions() {
        List<FreightJournalRecord> sourceTableData = new RusRubExchangeIncomeService().getPaymentTransactionEntry();

        FreightJournalRecord actualFreightJournalRecord = sourceTableData.get(0);

        createTransaction();

        assertEquals(expectedFreightJournalRecord.getIncomingPaymentList(), actualFreightJournalRecord.getIncomingPaymentList());
        assertEquals(expectedFreightJournalRecord.getOutgoingPaymentList(), actualFreightJournalRecord.getOutgoingPaymentList());

        assertEquals(expectedFreightJournalRecord.isAccountBalance()
                , actualFreightJournalRecord.isAccountBalance());

        assertEquals(expectedFreightJournalRecord.getActDate()
                , actualFreightJournalRecord.getActDate());

        assertEquals(expectedFreightJournalRecord.getActNumber()
                , actualFreightJournalRecord.getActNumber());

        assertEquals(expectedFreightJournalRecord.getCommission(), actualFreightJournalRecord.getCommission());
        assertEquals(expectedFreightJournalRecord.getActDateExchangeRate(), actualFreightJournalRecord.getActDateExchangeRate());

        assertEquals(expectedFreightJournalRecord.getCompletionCertificateVSPaymentExchangeIncome()
                , actualFreightJournalRecord.getCompletionCertificateVSPaymentExchangeIncome());

        assertEquals(expectedFreightJournalRecord.getCommissionExchangeIncome()
                , actualFreightJournalRecord.getCommissionExchangeIncome());

        assertEquals(expectedFreightJournalRecord.getReceivedVSPaidExchangeIncome()
                , actualFreightJournalRecord.getReceivedVSPaidExchangeIncome());

        assertEquals(expectedFreightJournalRecord.getAccountExchangeIncome().getIncomeAmount()
                , actualFreightJournalRecord.getAccountExchangeIncome().getIncomeAmount());

        assertEquals(expectedFreightJournalRecord.getAccountExchangeIncome().getJournalEntry()
                , actualFreightJournalRecord.getAccountExchangeIncome().getJournalEntry());

        assertEquals(expectedFreightJournalRecord.getReceivableAmount()
                , actualFreightJournalRecord.getReceivableAmount());

        assertEquals(expectedFreightJournalRecord.getPayableAmount()
                , actualFreightJournalRecord.getPayableAmount());

        assertEquals(expectedFreightJournalRecord, actualFreightJournalRecord);
    }
}
