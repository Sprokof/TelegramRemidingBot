package telegramBot.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class DateManage {


    public static String dayAndMonth(String date) {
        String month = detachMonthFromInputDate(date);
        if (month.charAt(month.length() - 1) == 'ь' || month.equals("май")) {
            month = (month.substring(0, month.length() - 1) + "я");
        } else {
            month = (month.substring(0, month.length()) + "а");
        }

        String day = date.split("\\p{P}")[0];
        if (day.startsWith("0")) {
            day = String.valueOf(day.charAt(1));
        }

        return String.format("%s %s", day, month);
    }

    private static String detachMonthFromInputDate(String date) {
        String intView = date.split("\\p{P}")[1];
        String month = null;
        switch (intView) {
            case "01":
                month = "январь";
                break;
            case "02":
                month = "февраль";
                break;
            case "03":
                month = "март";
                break;
            case "04":
                month = "апрель";
                break;
            case "05":
                month = "май";
                break;
            case "06":
                month = "июнь";
                break;
            case "07":
                month = "июль";
                break;
            case "08":
                month = "август";
                break;
            case "09":
                month = "сентябрь";
                break;
            case "10":
                month = "октябрь";
                break;
            case "11":
                month = "ноябрь";
                break;
            case "12":
                month = "декабрь";
                break;
        }
        return month;
    }
    public static String toNextMonth(String date) {
        String[] currentDate = date.split("\\.");
            currentDate[0] = "01";
            String day = currentDate[0];
            String month = "";
            if (currentDate[1].startsWith("0")) {
                month += ("0") + (Integer.parseInt(currentDate[1].substring(1)) + 1);
            } else {
                month += (Integer.parseInt(currentDate[1]) + 1);
            }
            String year = currentDate[2];

            return String.format("%s.%s.%s", day, month, year);
    }

    public static String nextDate(String date) {
       return parseToDate(date).plusDays(1).toString();
    }

    public static String currentDate() {
        String[] tempDates = Calendar.getInstance().toString().split(",");
        String day = tempDates[17].substring(tempDates[17].indexOf("=") + 1);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String month = String.valueOf(Integer.parseInt(tempDates[14].substring(tempDates[14].indexOf("=") + 1)) + 1);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String year = tempDates[13].substring(tempDates[13].indexOf("=") + 1);
        return String.format("%s.%s.%s", day, month, year);
    }

    public static String[] toDateArray() {
        return currentDate().split("\\.");
    }

    public static boolean validateDate(String day, String month, String year) {
        int result = 0;
        int dd = 0;
        int mm = 0;
        int yyyy = 0;
        try {
            dd = Integer.parseInt(day.trim());
            mm = Integer.parseInt(month);
            yyyy = Integer.parseInt(year.trim());
            if (day.startsWith("0")) {
                dd = Integer.parseInt(day.substring(1));
            }
            if (dd < 32 && dd >= 1) {
                result++;
            }

            if (month.startsWith("0")) {
                mm = Integer.parseInt(month.substring(1));
            }
            if (mm < 13 && mm >= 1) {
                result++;
            }

            if (yyyy >= Integer.parseInt(toDateArray()[2])) {
                result++;
            }
        } catch (NumberFormatException e) {
            result--;
        }
        if ((dd < Integer.parseInt(toDateArray()[0]) && (mm < Integer.parseInt(toDateArray()[1]))
                || (dd < Integer.parseInt(toDateArray()[0]) && (mm <= Integer.parseInt(toDateArray()[1])))
                || (dd >= Integer.parseInt(toDateArray()[0]) && (mm < Integer.parseInt(toDateArray()[1]))))) result--;


        return (result == 3);
    }

    public boolean isRemindDateBefore(String date){
        LocalDate current = parseToDate(currentDate());
        LocalDate remindDate = parseToDate(date);
    return remindDate.isBefore(current);


    }

    private static LocalDate parseToDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.ENGLISH);
        return LocalDate.parse(date, formatter);
    }
}
