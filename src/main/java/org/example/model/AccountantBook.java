package org.example.model;

//public class AccountantBook {
//
//    List<Transaction> transactionList;
//
//    public List<Transaction> getTransactionsListInRusRub() {
//        transactionList = new ArrayList<>();

//        for (List<Object> transactionsObjectsList : getTransactionsObjectsInRusRubList()) {
//            Transaction transaction = new Transaction();
//
//            BigDecimal incomingPaymentSum = retrieveNum(AccountantBookConstant.INCOMING_PAYMENT_SUM, transactionsObjectsList);
//            transaction.setIncomingPaymentSum(incomingPaymentSum);
//
//
//            transaction.setIncomingPaymentDate(retrieveStr(AccountantBookConstant.INCOMING_PAYMENT_DATE, transactionsObjectsList));
//
//            BigDecimal outgoingPaymentSum = retrieveNum(AccountantBookConstant.OUTGOING_PAYMENT_SUM, transactionsObjectsList);
//            transaction.setOutgoingPaymentSum(retrieveNum(AccountantBookConstant.OUTGOING_PAYMENT_SUM, transactionsObjectsList));
//
//            transaction.setOutgoingPaymentDate(retrieveStr(AccountantBookConstant.OUTGOING_PAYMENT_DATE, transactionsObjectsList));
//
//            transaction.setIncomes(countIncomes(incomingPaymentSum, outgoingPaymentSum));
//
//            transaction.setActDate(retrieveStr(AccountantBookConstant.ACT_DATE, transactionsObjectsList));
//
//            transaction.setAccountBalance(isBalance(transactionsObjectsList));
//
//            transaction.setActNumber(retrieveStr(AccountantBookConstant.ACT_NUMBER, transactionsObjectsList));
//
//            transactionList.add(transaction);
//        }
//        return transactionList;
//    }

//    private List<List<Object>> getTransactionsObjectsInRusRubList() {
//        return GoogleSheetHandler.filterTableByCellContent(SHEET_1_B_M, AccountantBookConstant.INCOMING_PAYMENT_SUM, RUS_RUB);
//    }
//
//    private BigDecimal retrieveNum(int index, List<Object> objects) {
//        return new BigDecimal(retrieveStr(index, objects).replaceAll("[а-я, А-Я]", ""));
//    }
//
//    private String retrieveStr(int index, List<Object> objects) {
//        return objects.get(index).toString().replace("г\\.?", "").trim();
//    }
//
//    private BigDecimal countIncomes(BigDecimal income, BigDecimal outgoings) {
//        return income.subtract(outgoings);
//    }
//
//    private boolean isBalance(List<Object> objects) {
//        return retrieveStr(AccountantBookConstant.ACCOUNT_BALANCE, objects).matches("\\d+");
//    }
//}
