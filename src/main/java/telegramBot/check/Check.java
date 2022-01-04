package telegramBot.check;

import java.util.Calendar;
import java.util.Locale;

public class Check {
    private static final String[] dates = Calendar.getInstance().toString().split(",");


    public static boolean date(String day, String mouth, String year){
        int result = 0;

        try{
            if(Integer.parseInt(day)<32&&Integer.parseInt(day)>=1||day.length()==2){
            result+=1;}}
        catch (NumberFormatException e){
            result-=1;}

        try{
            if(Integer.parseInt(mouth)<13&&Integer.parseInt(mouth)>=1){
            result+=1;}}
            catch(NumberFormatException e){
            result-=1;}

        try{
            if(Integer.parseInt(year)>=
                    Integer.parseInt(dates[13].substring(dates[13].indexOf("=")+1))){
            result+=1;}}
        catch (NumberFormatException e){result-=1;}

        return (result==3);}

    public static boolean time(String time){
    return time.matches("[0-9]{2}");}}
