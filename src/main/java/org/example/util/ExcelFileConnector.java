package org.example.util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.example.config.Config;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelFileConnector {
    public static final int FIRST_EXCEL_SHEET = 0;
    public static final String EXCEL_FILE_NAME_KEY = "rate_excel_file";
    public static final String EXCEL_FILE_PATH = Config.getProperties().getProperty(EXCEL_FILE_NAME_KEY);


    public static Sheet connectToSheets() throws IOException {
        HSSFSheet sheet;
        FileInputStream exchangeRateFile = new FileInputStream(EXCEL_FILE_PATH);
        HSSFWorkbook workbook = new HSSFWorkbook(exchangeRateFile);
        sheet = workbook.getSheetAt(FIRST_EXCEL_SHEET);

        return sheet;
    }
}
