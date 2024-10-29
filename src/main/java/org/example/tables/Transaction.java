package org.example.tables;

import lombok.Getter;
import lombok.Setter;
import org.example.tables.exchange_rate_table.ExchangeRate;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    private BigDecimal incomingPaymentSum;
    private String incomingPaymentDate;
    private BigDecimal outgoingPaymentSum;
    private String outgoingPaymentDate;
    private boolean accountBalance;
    private String actDate;
    private BigDecimal incomes;
    private String actNumber;

    private ExchangeRate incomingPaymentExchangeRate;
    private ExchangeRate actDateExchangeRate;
    private ExchangeRate outgoingPaymentExchangeRate;
}
