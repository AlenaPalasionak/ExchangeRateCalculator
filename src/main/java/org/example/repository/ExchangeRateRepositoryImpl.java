package org.example.repository;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.config.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.example.constants.AccountantBookConstant.DATE_FORMAT;
import static org.example.constants.ExcelConstants.FIRST_EXCEL_SHEET;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {
    private Map<String, BigDecimal> exchangeRateTableCache;

    public Map<String, BigDecimal> getExchangeRateTableCache() {
        if (exchangeRateTableCache == null)
            exchangeRateTableCache = loadExchangeRateTable();
        return exchangeRateTableCache;
    }

    private Map<String, BigDecimal> loadExchangeRateTable() {
        Map<String, BigDecimal> exchangeRateTable = new LinkedHashMap<>();
        try (FileInputStream exchangeRateFile = new FileInputStream(Config.EXCEL_FILE_PATH);
             HSSFWorkbook workbook = new HSSFWorkbook(exchangeRateFile)) {
            HSSFSheet sheet = workbook.getSheetAt(FIRST_EXCEL_SHEET);

            int startRow = 5;
            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row != null) {
                    Cell cellA = row.getCell(0);
                    Cell cellAW = row.getCell(48);

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
            throw new RuntimeException(e);
        }
        return exchangeRateTable;
    }
}
