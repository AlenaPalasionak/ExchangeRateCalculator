package org.example.google_sheet_handler;

import com.google.api.services.sheets.v4.Sheets;

public class DataTransferor {
    protected final Sheets service = SheetsStart.getService();
    protected final String spreadsheetId;
    private String EMPTY_ROW_NUMBER_COORDINATES;
    public static final String NOT_TO_BE_FILLED_SELL = "";
    private static final String H1_H_RANGE = "!H1:H";

    public DataTransferor(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

//    public void addValueToSpreadSheets(String sheetName) {
//        Log.info("(AbstractDataTransferor) 3. Values are gonna be added to Sheet " + sheetName
//                + ". Storage directory is gonna be opened in case it exists ");
//        List<Transportation> transportationList = FileHandler.getNewTransportationsList(storageDir);
//        for (Transportation tr : transportationList) {
//            String carrierName = tr.carrierName();
//            String clientName = tr.clientName();
//            String date = tr.date();
//            String price = tr.price();
//            String driver = tr.driver();
//            ValueRange body = new ValueRange()
//                    .setValues(List.of(
//                            List.of(clientName, NOT_TO_BE_FILLED_SELL
//                                    , carrierName, driver, price
//                                    , date, NOT_TO_BE_FILLED_SELL, getNextNumber(sheetName))));
//            update(body);
//        }
//        FileHandler.markAsWritten(storageDir);
//    }
//
//    private int getLastNumber(String sheetName) {
//        final ValueRange numerationRageResponseH1H;
//        String h1HRangeOfSheet = sheetName + H1_H_RANGE;
//        try {
//            numerationRageResponseH1H = service.spreadsheets().values()
//                    .get(spreadsheetId, h1HRangeOfSheet)
//                    .execute();
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, DialogPaneMessage.WRONG_MONTH_ISSUE);
//            Log.info("(AbstractDataTransferor) 4. Last Number was gonna be got, but the Exception was thrown " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//        List<List<Object>> numerationRowsListH1H = numerationRageResponseH1H.getValues();
//        final int numerationRowsListH1HSize = numerationRowsListH1H.size();
//        EMPTY_ROW_NUMBER_COORDINATES = String.format(sheetName + "!" + "A%s", numerationRowsListH1HSize + 1);
//        String numberOfSell = (String) numerationRowsListH1H.get(numerationRowsListH1HSize - 1).get(0);
//        if (isListEmpty(numberOfSell)) {
//            return 0;
//        } else {
//            List<Object> lastRowList = numerationRowsListH1H.get(numerationRowsListH1H.size() - 1);
//            Object lastCellValue = lastRowList.get(0);
//            return Integer.parseInt(String.valueOf(lastCellValue));
//        }
//    }
//
//    private boolean isListEmpty(String numberOfSell) {
//        return Objects.equals(numberOfSell, "№");
//    }
//
//    private int getNextNumber(String sheetName) {
//        return getLastNumber(sheetName) + 1;
//    }
//
//    private void update(ValueRange body) {
//        try {
//            service.spreadsheets().values()
//                    .update(spreadsheetId, EMPTY_ROW_NUMBER_COORDINATES, body)
//                    .setValueInputOption("RAW")
//                    .execute();
//        } catch (IOException e) {
//            Log.info("(AbstractDataTransferor) 5. Spreadsheet was gonna be updated, but the Exception was thrown " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}
