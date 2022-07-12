package telegramBot.manage;

import java.util.Calendar;

public class TimeManage {

    public static String currentTime() {
        String[] params = Calendar.getInstance().toString().split(",");
        String hour; String minutes = params[24].
                substring(params[24].indexOf("=")+1);

        if (params[21].equals("AM_PM=1")) {
            hour = String.valueOf(Integer.parseInt(params[22].
                    substring(params[22].indexOf("=") + 1)) + 12);
        } else {
            hour = String.valueOf(Integer.parseInt(params[22].
                    substring(params[22].indexOf("=") + 1)));
        }
        if(minutes.length() == 1) minutes = "0" +minutes;
        return String.format("%s:%s", hour, minutes);
    }

    public static double toDoubleTime(String time){
        return Double.parseDouble(time.replace(':', '.'));
    }

    public static double timeDifference(String lastSendTime) {
        double current = Double.parseDouble(currentTime().replace(':', '.'));
        double last = Double.parseDouble(lastSendTime.replace(':', '.'));
        return current - last;
}
    public static String toStringTime(double time){
        return String.valueOf(time).replace(".", ":");
    }
}
