package org.example.util;

import java.math.BigDecimal;
import java.util.List;

public class StringHelper {

    public static BigDecimal retrieveNumberFromString(List<Object> rowObject, int index) {
        return new BigDecimal(deleteYearSignFromStringDate(rowObject, index).replaceAll("[а-я, А-Я]", ""));
    }

    public static String deleteYearSignFromStringDate(List<Object> rowObject, int index) {
        return rowObject.get(index).toString().replace("г\\.?", "").trim();
    }

    public static String retrieveLettersFromString(List<Object> rowObject, int index) {
        return rowObject.get(index).toString().replace("\\d+", "").trim();
    }
}
