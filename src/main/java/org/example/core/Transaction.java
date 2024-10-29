package org.example.core;

import java.math.BigDecimal;

public class Transaction {
    BigDecimal incomingPaymentSum;
    String incomingPaymentDate;
    BigDecimal outgoingPaymentSum;
    String outgoingPaymentDate;
    boolean accountBalance;
    String actDate;
    BigDecimal incomes;
    String actNumber;
    public BigDecimal getIncomingPaymentSum() {
        return incomingPaymentSum;
    }

    public void setIncomingPaymentSum(BigDecimal incomingPaymentSum) {
        this.incomingPaymentSum = incomingPaymentSum;
    }

    public String getIncomingPaymentDate() {
        return incomingPaymentDate;
    }

    public void setIncomingPaymentDate(String incomingPaymentDate) {
        this.incomingPaymentDate = incomingPaymentDate;
    }

    public BigDecimal getOutgoingPaymentSum() {
        return outgoingPaymentSum;
    }

    public void setOutgoingPaymentSum(BigDecimal outgoingPaymentSum) {
        this.outgoingPaymentSum = outgoingPaymentSum;
    }

    public String getOutgoingPaymentDate() {
        return outgoingPaymentDate;
    }

    public void setOutgoingPaymentDate(String outgoingPaymentDate) {
        this.outgoingPaymentDate = outgoingPaymentDate;
    }

    public boolean hasAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(boolean accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getActDate() {
        return actDate;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }

    public BigDecimal getIncomes() {
        return incomes;
    }

    public void setIncomes(BigDecimal incomes) {
        this.incomes = incomes;
    }

    public String getActNumber() {
        return actNumber;
    }

    public void setActNumber(String actNumber) {
        this.actNumber = actNumber;
    }
}
