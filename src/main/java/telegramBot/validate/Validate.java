package telegramBot.validate;

import java.util.Calendar;

public class Validate {

    public static boolean date(String day, String month, String year){
        int result = 0;
        int dd = 0;
        int mm = 0;
        int yyyy = 0;
    try{
        dd = Integer.parseInt(day.trim());
        mm = Integer.parseInt(month);
        yyyy = Integer.parseInt(year.trim());
         if(day.startsWith("0")) {
                dd = Integer.parseInt(day.substring(1));}
            if(dd < 32 && dd >= 1){
            result ++;}

            if(month.startsWith("0")) {
                mm = Integer.parseInt(month.substring(1));}
            if(mm < 13 && mm >= 1){
            result ++;}

            if(yyyy >= Integer.parseInt(date()[2])){
            result ++;}}
        catch (NumberFormatException e){ result -- ;}
        if((dd<Integer.parseInt(date()[0])&&(mm<Integer.parseInt(date()[1]))
        ||(dd<Integer.parseInt(date()[0])&&(mm<=Integer.parseInt(date()[1])))
        ||(dd>=Integer.parseInt(date()[0])&&(mm<Integer.parseInt(date()[1]))))) result --;


        return  (result == 3) ;}


    private static String[] date(){
        String[] tempDates = Calendar.getInstance().toString().split(",");
        String day = tempDates[17].substring(tempDates[17].indexOf("=")+1);
        String mouth = String.valueOf(Integer.parseInt(tempDates[14].substring(tempDates[14].indexOf("=")+1))+1);
        String year = tempDates[13].substring(tempDates[13].indexOf("=")+1);
        return String.format("%s.%s.%s", day, mouth, year).split("\\.");
    }

    public static String codedMaintenance(String maintenance){
        char[] chars = maintenance.toCharArray();
        String codedMaintenance = "";
        for(char c : chars){ codedMaintenance += (int)c + "/"; }
        return codedMaintenance;
    }
    public static String decodedMaintenance(String codedMaintenance){
        String[] intStrings = codedMaintenance.split("\\/");
        String decodedMaintenance = "";
        for(String i : intStrings){ decodedMaintenance += (char)Integer.parseInt(i); }
        return decodedMaintenance;
    }
}

