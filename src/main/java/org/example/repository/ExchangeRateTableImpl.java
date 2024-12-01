package org.example.repository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.example.constants.ExcelConstants;
import org.example.exception.ExcelSheetAccessException;
import org.example.util.ExcelFileConnector;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.example.constants.AccountantBookConstants.DATE_FORMAT;

public class ExchangeRateTableImpl implements ExchangeRateTable {
    private Map<String, BigDecimal> exchangeRateTableCache = null;

    public Map<String, BigDecimal> getExchangeRateTableCache() {
        if (exchangeRateTableCache == null)
            exchangeRateTableCache = loadExchangeRateTable();
        return exchangeRateTableCache;
    }

    private Map<String, BigDecimal> loadExchangeRateTable() {
        Map<String, BigDecimal> exchangeRateTable = new LinkedHashMap<>();
        try {
            Sheet sheet = ExcelFileConnector.connectToSheets();

            int startRow = 5;
            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row != null) {
                    Cell cellA = row.getCell(ExcelConstants.DATE_ROW);//date
                    Cell cellAW = row.getCell(ExcelConstants.RATE_ROW);//rate

                    if (cellA != null && cellAW != null) {
                        if (cellA.getCellType() == CellType.BLANK && cellAW.getCellType() == CellType.BLANK) {
                            break;
                        }
                        Date rateDate = cellA.getDateCellValue();
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                        String formattedExchangeRateDate = dateFormat.format(rateDate);
                        BigDecimal exchangeRate = BigDecimal.valueOf(cellAW.getNumericCellValue());
                        exchangeRateTable.put(formattedExchangeRateDate, exchangeRate);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExcelSheetAccessException("Не удалось получить доступ к Excel таблице", e);
        }

        return exchangeRateTable;
    }
}
