package org.example.util;

import java.math.BigDecimal;

public class StringHelper {

    public static BigDecimal retrieveNumberFromString(String string) {
        BigDecimal bigDecimal;
        bigDecimal = new BigDecimal(string.replaceAll("[^0-9]", ""));
        //      Log.info("Ошибка формата числа " + string + ", " + deleteLettersFromStringDate(string).replaceAll("[а-я, А-Я]", "").trim());
        return bigDecimal;
    }

    public static String retrieveDateFromString(String date) {
        return date.replaceAll("[^0-9.]", "").replaceAll("\\.?$", "");
    }

    public static String retrieveLettersFromString(String string) {
        String sum = String.valueOf(string);

        return sum.replaceAll("\\d+", "").trim();
    }
}
