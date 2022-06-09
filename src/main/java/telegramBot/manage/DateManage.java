package telegramBot.manage;

import java.util.Calendar;
import java.util.HashMap;

public class DateManage {

    public static final String DEFAULT_TIME = "00:00";

    public static final HashMap<String, String> lastDayInMonth = new HashMap<>();

    static {
        lastDayInMonth.put("01", "31.01");
        lastDayInMonth.put("02", "28.02");
        lastDayInMonth.put("03", "30.03");
        lastDayInMonth.put("04", "30.04");
        lastDayInMonth.put("05", "31.05");
        lastDayInMonth.put("06", "30.06");
        lastDayInMonth.put("07", "31.07");
        lastDayInMonth.put("08", "31.08");
        lastDayInMonth.put("09", "30.09");
        lastDayInMonth.put("10", "31.10");
        lastDayInMonth.put("11", "30.11");
        lastDayInMonth.put("12", "31.12");


    }

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
        String[] thisDate = date.split("");
        String nextDate = String.format(thisDate[0] + "%d" + thisDate[2] +
                "" + thisDate[3] + "" + thisDate[4] + "" + thisDate[5] + "" +
                thisDate[6] + "" + thisDate[7] + "" + thisDate[8] + "" + thisDate[9], Integer.parseInt(thisDate[1]) + 1);

        if (nextDate.startsWith("0") && nextDate.indexOf(".") == 3) {
            nextDate = nextDate.substring(1);
        }

        if (nextDate.indexOf(".") == 3) {
            nextDate = String.format("%d" + thisDate[2] +
                            "" + thisDate[3] + "" + thisDate[4] + "" + thisDate[5] + "" +
                            thisDate[6] + "" + thisDate[7] + "" + thisDate[8] + "" + thisDate[9],
                    Integer.parseInt(thisDate[0] + thisDate[1]) + 1);
        }

        String lastDate = lastDayInMonth.get(nextDate.substring(nextDate.indexOf(".") + 1,
                nextDate.lastIndexOf(".")));

        if ((Integer.parseInt(nextDate.substring(0, nextDate.indexOf("."))) - 1)
                == Integer.parseInt(lastDate.substring(0, lastDate.indexOf(".")))) {
            nextDate = DateManage.toNextMonth(nextDate);
        }

        return nextDate;
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

    public static boolean isRemindDateBeforeCurrent(String date){
        String[] currentDate = currentDate().
                replaceAll("0", "").
                split("\\.");

        String[] remindDate = date.
                replaceAll("0", "").
                split("\\.");

        int index = 0;

        while(index != remindDate.length) {

            int rd = (Integer.parseInt(remindDate[index]));

            int cd = (Integer.parseInt(currentDate[index]));

            if (rd < cd) {
                return true;
            }

            index++;

        }
    return false;

    }

}
