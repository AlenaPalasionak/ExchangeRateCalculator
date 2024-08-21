package org.example.util.google_sheet_handler;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.config.Config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleSheetHandler {
    protected static final Sheets service = SheetsStart.getService();
    protected static final String accountantBookSpreadsheetId = Config.getProperties()
            .getProperty("accountant_book_spreadsheetId");

    public static List<List<Object>> getListsWithCellContent(String range, int cellIndexName, String content) {
        List<List<Object>> transactions = GoogleSheetHandler.getRangeList(range);
        transactions = transactions.stream().filter(transaction -> String.valueOf(transaction
                .get(cellIndexName)).contains(content)).collect(Collectors.toList());
        return transactions;
    }

    private static List<List<Object>> getRangeList(String range) {
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
