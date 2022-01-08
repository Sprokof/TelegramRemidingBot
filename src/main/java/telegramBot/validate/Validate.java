package telegramBot.validate;

import java.util.Calendar;

public class Validate {

    public static boolean date(String day, String mouth, String year){
        int result = 0;
        int dd = Integer.parseInt(day.trim());
        int mm = Integer.parseInt(mouth.trim());
        int yyyy = Integer.parseInt(year.trim());

        try{
            if(day.startsWith("0")) {
                dd = Integer.parseInt(day.substring(1));}
            if(dd < 32 && dd >= 1){
            result ++;}

            if(mouth.startsWith("0")) {
                mm = Integer.parseInt(mouth.substring(1));}
            if(mm < 13 && mm >= 1){
            result ++;}

            if(yyyy >= Integer.parseInt(date()[2])){
            result ++;}}
        catch (NumberFormatException e){ result -- ;}
        if(dd<Integer.parseInt(date()[0])||mm<Integer.parseInt(date()[1])) result --;

        return  (result == 3) ;}


    private static String[] date(){
        String[] tempDates = Calendar.getInstance().toString().split(",");
        String day = tempDates[17].substring(tempDates[17].indexOf("=")+1);
        String mouth = String.valueOf(Integer.parseInt(tempDates[14].substring(tempDates[14].indexOf("=")+1))+1);
        String year = tempDates[13].substring(tempDates[13].indexOf("=")+1);
        return String.format("%s.%s.%s", day, mouth, year).split("\\.");
    }}

