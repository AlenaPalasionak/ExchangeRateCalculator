package org.example.util;

public class DateConverter {

    private static final int dayIndex = 0;
    private static final int monthIndex = 1;
    private static final int yearIndex = 2;

    public static String convertDate(String date) {
        String[] dateParts = date.split("\\.");
        StringBuilder exchangeDate = new StringBuilder();
        String day = dateParts[dayIndex];
        if (day.startsWith("0")) {
            day = day.substring(1);
        }
        String month = dateParts[monthIndex];
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        String year = dateParts[yearIndex];

        exchangeDate.append(month).append("/").append(day).append("/").append(year);

        //  System.out.println(exchangeDate);
        return exchangeDate.toString();
    }
}