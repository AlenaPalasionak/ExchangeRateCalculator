package org.example.tables.counter;

import java.math.BigDecimal;

public abstract class ExchangeRateCounterImpl implements ExchangeRateCounter {
    BigDecimal averageExchangeRate1;
    BigDecimal averageExchangeRate2;
    BigDecimal averageSum;

//    @Override
//    public BigDecimal count(List<BigDecimal> exchangeRate1, List<BigDecimal> exchangeRate2, List<BigDecimal> sum) {
//        if (exchangeRate1.size() == 1 && exchangeRate2.size() == 1) {
//            return (exchangeRate1.get(0).subtract(exchangeRate2.get(0)))
//                    .multiply(sum.get(0));
//        }
//        if (exchangeRate1.size() == 1 && exchangeRate2.size() == 2) {
//            return (exchangeRate1.get(0).subtract(
//                            (exchangeRate2.get(0))).multiply(sum.get(0))
//                    .add
//                            ((exchangeRate2.get(1))).multiply(sum.get(1)));
//        }
//
//        if
//        return new BigDecimal("1");
//    }
//
//    private BigDecimal getAverageExchangeRate1(List<BigDecimal> exchangeRate, List<BigDecimal> sum) {
//for(e)
   // }
}
