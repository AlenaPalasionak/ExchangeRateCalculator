package org.example.util.google_sheet_handler;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.config.Config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleSheetHandler {
    private static final Sheets service = SheetsStart.getService();
    private static final String accountantBookSpreadsheetId = Config.getProperties()
            .getProperty("accountant_book_spreadsheetId");

    public static List<List<Object>> filterTableByCellContent(String range, int cellIndexName, String cellContent) {
        List<List<Object>> transactions = GoogleSheetHandler.getSheetDataAsTable(range);
        transactions = transactions.stream().filter(transaction -> String.valueOf(transaction
                .get(cellIndexName)).contains(cellContent)).collect(Collectors.toList());
        return transactions;
    }

    private static List<List<Object>> getSheetDataAsTable(String range) {
        List<List<Object>> transactions;
        try {
            ValueRange result = service.spreadsheets().values()
                    .get(accountantBookSpreadsheetId, range).execute();
            transactions = result.getValues();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }
}
