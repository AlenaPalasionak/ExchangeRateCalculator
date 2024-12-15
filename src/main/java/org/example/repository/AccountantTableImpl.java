package org.example.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.config.Config;
import org.example.exception.GoogleSheetsAccessException;
import org.example.util.GoogleSheetsConnector;

import java.io.IOException;
import java.util.List;

public class AccountantTableImpl implements AccountantTable {
    private static List<List<Object>> accountantBookTableCache = null;
    public static final String ACCOUNTANT_BOOK_SPREADSHEET_ID_KEY = "accountant_book_spreadsheetId";
    public static final String ACCOUNTANT_BOOK_SPREADSHEET_ID = Config.getProperties().getProperty(ACCOUNTANT_BOOK_SPREADSHEET_ID_KEY);

    @Override
    public List<List<Object>> getSheetDataTableCache(String range) {
        if (accountantBookTableCache == null) {
            accountantBookTableCache = loadGoogleSheetTable(range);
        }
        return accountantBookTableCache;
    }

    public void refreshAccountantBookTable(String range) {// сделать кнопку в интерфейсе GUI
        accountantBookTableCache = loadGoogleSheetTable(range);
    }

    private static List<List<Object>> loadGoogleSheetTable(String range) {
        List<List<Object>> accountantBookTable;
        final Sheets service;
        try {
            service = GoogleSheetsConnector.connectToSheets();
            ValueRange result = service.spreadsheets().values()
                    .get(ACCOUNTANT_BOOK_SPREADSHEET_ID, range).execute();
            accountantBookTable = result.getValues();
        } catch (IOException e) {
            throw new GoogleSheetsAccessException("Не удалось получить доступ к Google таблице", e);
        }
        return accountantBookTable;
    }
}
