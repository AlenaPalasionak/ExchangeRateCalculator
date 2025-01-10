package org.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FreightJournalRecord {

    private BigDecimal receivableAmount;//2
    private BigDecimal payableAmount;//5
    private List<Payment> incomingPaymentList;//4,
    private List<Payment> outgoingPaymentList;//7
    private boolean accountBalance;
    private String actDate;//last - p
    private BigDecimal commission;//6
    private String actNumber;//1
    private ExchangeRate actDateExchangeRate;//3


}
