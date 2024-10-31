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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExchangeRateRepositoryImpl {
    private static final String excelFilePath = Config.getProperties().getProperty("rate_excel_file");
    Map<String, BigDecimal> dataMap = new HashMap<>();

    public Map<String, BigDecimal> getRateMap() {
        Map<String, BigDecimal> dataMap = new LinkedHashMap<>();

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
                        dataMap.put(formattedDate, value);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dataMap;
    }
}
