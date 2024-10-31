package org.example.repository;

import org.apache.commons.collections4.map.SingletonMap;
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
import java.util.*;

public class SingletonExchangeRate {
    private static SingletonExchangeRate exchangeRateInstance;
    private static final String excelFilePath = Config.getProperties().getProperty("rate_excel_file");
    private final Map<String, BigDecimal> exchangeRateMap;

    private SingletonExchangeRate() {
        exchangeRateMap = new LinkedHashMap<>();
        try (FileInputStream file = new FileInputStream(excelFilePath);
             HSSFWorkbook workbook = new HSSFWorkbook(file)) {
            HSSFSheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

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
                        Date key = cellA.getDateCellValue();
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        String formattedDate = format.format(key);
                        BigDecimal value = BigDecimal.valueOf(cellAW.getNumericCellValue());
                        exchangeRateMap.put(formattedDate, value);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Шаг 4: Метод для получения единственного экземпляра
    public static SingletonExchangeRate getExchangeRateInstance() {
        if (exchangeRateInstance == null) {
            synchronized (SingletonMap.class) {
                if (exchangeRateInstance == null) {
                    exchangeRateInstance = new SingletonExchangeRate();
                }
            }
        }
        return exchangeRateInstance;
    }

    public Map<String, BigDecimal> getMap() {
        return Collections.unmodifiableMap(exchangeRateMap);
    }
}
