package org.example.repository;

import org.example.config.Config;

public class ExchangeDifferenceRepositoryImpl  implements ExchangeDifferenceRepository{
    public static final String RATE_DIFFERENCE_SPREADSHEET_ID_KEY ="rate_difference_spreadsheet";
    public static final String RATE_DIFFERENCE_SPREADSHEET_ID = Config.getProperties().getProperty(RATE_DIFFERENCE_SPREADSHEET_ID_KEY);


    // method write
//    ValueRange body = new ValueRange()
//            .setValues(List.of(
//                    List.of(clientName, NOT_TO_BE_FILLED_SELL
//                            , carrierName, driver, price
//                            , date, NOT_TO_BE_FILLED_SELL, getNextNumber(sheetName))));
//    update(body);
}
