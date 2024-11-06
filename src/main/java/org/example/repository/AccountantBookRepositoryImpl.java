package org.example.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.exception.GoogleSheetsAccessException;
import org.example.util.GoogleSheetsConnector;

import java.io.IOException;
import java.util.List;

import static org.example.config.Config.SPREADSHEET_ID;

public class AccountantBookRepositoryImpl implements AccountantBookRepository {
    private List<List<Object>> accountantBookTableCache = null;

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

    private List<List<Object>> loadGoogleSheetTable(String range) {
        List<List<Object>> accountantBookTable;
        final Sheets service;
        try {
            service = GoogleSheetsConnector.connectToSheets();
            ValueRange result = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range).execute();
            accountantBookTable = result.getValues();
        } catch (IOException e) {
            throw new GoogleSheetsAccessException("Не удалось получить доступ к Google таблице", e);
        }
        return accountantBookTable;
    }
}
