package org.example.config;

import org.example.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class Config {

    private static final Properties PROPERTIES = new Properties();
    public static final String SPREADSHEET_ID = getProperties().getProperty("accountant_book_spreadsheetId");
    public static final String EXCEL_FILE_PATH = getProperties().getProperty("rate_excel_file");

    public static Properties getProperties() {
        Reader reader;
        if (PROPERTIES.isEmpty()) {
            String fileName = "file.properties";
            Log.info("(Config) 1. try to load PROPERTIES (properties file name is " + fileName + ")");
            try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(fileName)) {
                reader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
                PROPERTIES.load(reader);
            } catch (IOException e) {
                Log.info("(Config) 2. Exception while getting properties object" + e);
                throw new RuntimeException("Invalid config file");
            }
        }
        Log.info("(Config) 3. Properties:  " + PROPERTIES);

        return PROPERTIES;
    }
}



