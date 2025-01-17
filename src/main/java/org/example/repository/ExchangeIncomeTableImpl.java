//package org.example.repository;
//
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.model.ValueRange;
//import org.example.config.Config;
//import org.example.model.FreightJournalRecord;
//import org.example.model.PaymentTransactionEntry;
//import org.example.model.non_operating_income.AbstractExchangeIncome;
//import org.example.service.RusRubExchangeIncomeService;
//import org.example.util.GoogleSheetsConnector;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//public class ExchangeIncomeTableImpl implements ExchangeIncomeTable {
//    public static final String RATE_DIFFERENCE_SPREADSHEET_ID_KEY = "rate_difference_spreadsheet";
//    public final String RATE_DIFFERENCE_SPREADSHEET_ID = Config.getProperties().getProperty(RATE_DIFFERENCE_SPREADSHEET_ID_KEY);
//    private static final String RANGE = "Sheet1!A1";
//    private static final List<PaymentTransactionEntry> SOURCE_TABLE_DATA = new RusRubExchangeIncomeService().getPaymentTransactionEntryList();
//    PaymentTransactionEntry paymentTransactionEntry;
//
//    // Маппинг journalEntry -> индекс столбца (H=7, I=8 и т.д.)
//    private static final Map<String, Integer> COLUMN_MAPPING = Map.of(
//            "62/11-60/11", 7,
//            "62/11-90/7", 8,
//            "60/11-90/7", 9,
//            "60/11-62/11", 10,
//            "90/4-62/11", 11,
//            "90/4-60/11", 12,
//            "52/1-60/11", 13,
//            "60/11-52/1", 14
//    );


//    public void writeData(List<FreightJournalRecord> sourceTableData) {
//        try {
//            List<List<Object>> rows = new ArrayList<>();
//
//            for (FreightJournalRecord freightJournalRecord : sourceTableData) {
//                List<Object> row = new ArrayList<>();
//
//                // Заполнение столбцов A-G
//                row.add(freightJournalRecord.getActNumber());
//                row.add(freightJournalRecord.getReceivableAmount());
//                row.add(freightJournalRecord.getActDateExchangeRate());
//                row.add(freightJournalRecord.getIncomingPaymentRate());
//                row.add(freightJournalRecord.getIncomingPaymentAmountDividedBy100());
//                row.add(freightJournalRecord.getCommissionDividedBy100());
//                row.add(freightJournalRecord.getOutgoingPaymentRate());
//
//                // Инициализация пустых столбцов для H-O
//                List<Object> dynamicColumns = new ArrayList<>(Collections.nCopies(8, ""));
//
//                // Заполнение столбцов H-O на основе journalEntry
//                fillDynamicColumns(dynamicColumns, freightJournalRecord);
//
//                row.addAll(dynamicColumns);
//                rows.add(row);
//            }
//
//            // Отправка данных в Google Таблицу
//            Sheets sheetsService = GoogleSheetsConnector.connectToSheets();
//            ValueRange body = new ValueRange().setValues(rows);
//            sheetsService.spreadsheets().values()
//                    .update(RATE_DIFFERENCE_SPREADSHEET_ID_KEY, RANGE, body)
//                    .setValueInputOption("RAW")
//                    .execute();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void fillDynamicColumns(List<Object> dynamicColumns, FreightJournalRecord freightJournalRecord) {
//        List<AbstractExchangeIncome> incomes = List.of(
//                freightJournalRecord.getCompletionCertificateVSPaymentExchangeIncome(),
//                freightJournalRecord.getCommissionExchangeIncome(),
//                freightJournalRecord.getReceivedVSPaidExchangeIncome(),
//                freightJournalRecord.getAccountExchangeIncome()
//        );// здесь еще поработать в зависимости от того что будем получать
//
//        for (AbstractExchangeIncome income : incomes) {
//            if (income != null && income.getJournalEntry() != null) {
//                Integer columnIndex = COLUMN_MAPPING.get(income.getJournalEntry());
//                if (columnIndex != null) {
//                    // Заполнение столбца H-O соответствующим значением
//                    dynamicColumns.set(columnIndex - 7, income.getExchangeIncomeAmount());
//                }
//            }
//        }
//    }
//
//
//    public void fillData(FreightJournalRecord freightJournalRecord) {
//        actNumber = freightJournalRecord.getActNumber();
//        receivableAmount = freightJournalRecord.getReceivableAmount();
//        actDateExchangeRate = freightJournalRecord.getActDateExchangeRate();
////         incomingPaymentRate = transaction.getIncomingPaymentList().;//входящий курс оплаты нам
////         outgoingPaymentAmount = transaction.getOutgoingPaymentList();//сумма перевозчику; курс оплаты перевозчику
//        commissionDividedBy100 = (freightJournalRecord.getCommission()).divide(new BigDecimal("100"));// делить на 100
//
////         completionCertificateVSPaymentExchangeIncome ;
////         commissionExchangeIncome;
////         accountExchangeIncome;
//
//        String actDate = freightJournalRecord.getActNumber();
//
//    }
//}
//    public void writeData(List<Transaction> transactions) {
//        try {
//            for (Transaction transaction : transactions) {
//                fillData(transaction);
//                Sheets sheetsService = GoogleSheetsConnector.connectToSheets();
//                ValueRange body = new ValueRange()
//                        .setValues(List.of(
//                                List.of(actNumber, receivableAmount
//                                        , actDateExchangeRate, incomingPaymentRate
//                                        , incomingPaymentAmountDividedBy100, commissionDividedBy100
//                                        , outgoingPaymentRate, ))));
//
//                sheetsService.spreadsheets().values().
//                        update(RATE_DIFFERENCE_SPREADSHEET_ID, RANGE, body)
//                        .setValueInputOption("RAW")
//                        .execute();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//       }
//    }
//
