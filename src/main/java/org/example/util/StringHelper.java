package org.example.util;

import java.math.BigDecimal;

public class StringHelper {

    public static BigDecimal retrieveNumberFromString(String string) {
        BigDecimal bigDecimal = null;
      //  try {
            bigDecimal = new BigDecimal(deleteLettersFromStringDate(string).replaceAll("[а-я, А-Я]", "").trim());
            System.out.println(bigDecimal); // сделать Log здесь и проверку на содержит ли bigDecimal только одну точку
      //  } catch (NumberFormatException e) {
      //      Log.info("Ошибка формата числа " + string + ", " + deleteLettersFromStringDate(string).replaceAll("[а-я, А-Я]", "").trim());
      //  }
        return bigDecimal;
    }

    public static String deleteLettersFromStringDate(String date) {
        return date.replaceAll("[a-zA-Zа-яА-ЯгГ]+|г\\.", "").trim();
    }

    public static String retrieveLettersFromString(String string) {
        String sum = String.valueOf(string);

        return sum.replaceAll("\\d+", "").trim();
    }
}
