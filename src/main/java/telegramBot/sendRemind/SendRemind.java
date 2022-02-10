package telegramBot.sendRemind;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Remind;
import telegramBot.hidenPackage.RemindForDefPerson;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import java.util.*;

public class SendRemind {
    public static final HashMap<String, String> lastDayInMonth = new HashMap<>();

    static{
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

    private final SendMessageServiceImpl service;
    private static boolean stop = false;
    private static final String REMIND_MESSAGE = "Позвольте напомнить, что вам нужно ";

    @Autowired
    public SendRemind(SendMessageService service) {
        this.service = (SendMessageServiceImpl) service;
    }

    public void executeRemindMessage() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
        try{
            findRemindToSend();
            }
        catch (InterruptedException e){e.printStackTrace();
        }}};
        timer.scheduleAtFixedRate(task, 3500, 60000);
    }


    private synchronized int[] getIdOfAllReminds() throws InterruptedException {
        List<Remind> reminds;
        while ((reminds = RemindServiceImpl.newRemindService().
                getAllRemindsFromDB()).size() <= 1) {
            wait();
        }
        notify();
        int[] ides = null;
        try {
            ides = new int[reminds.size()];
            String id;
            Remind remind;
            for (int i = 0; i < ides.length; i++) {
                remind = reminds.get(i);
                id = remind.toString().
                        substring(remind.toString().indexOf("=") + 1, remind.toString().indexOf(","));
                ides[i] = Integer.parseInt(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ides;
    }

    private synchronized void findRemindToSend() throws InterruptedException {
        int[] remindId = getIdOfAllReminds();
        String executeDate;
        List<Remind> remindsOneTime = new ArrayList<>();
        List<Remind> remindsSeveralTime = new ArrayList<>();
        String maintenance;
        stop();
        for (int index = 0; index < remindId.length; index++) {
            if (noDelete(remindId[index])) continue;
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindId[index]);
            executeDate = remind.getRemindDate();
            if (isConditionsToSendOneTime(executeDate, currentDate(), remind)) {
                remindsOneTime.add(remind); }
            else if(isConditionsToSendDaily(executeDate, currentDate(), remind)){
                remindsSeveralTime.add(remind);}
        }
        if(!remindsOneTime.isEmpty() && remindsSeveralTime.isEmpty()){
        if(remindsOneTime.size()>1){
            maintenance = messageForSeveralRemind(remindId,((Remind[]) remindsOneTime.toArray()));
            if(this.service.sendMessage(remindsOneTime.get(0).getUserChatID(), maintenance)){
                remindsOneTime.clear();}
                }
                else if(remindsOneTime.size() == 1){
                maintenance = (Character.toLowerCase(remindsOneTime.get(0).getMaintenance().
                        charAt(0))+remindsOneTime.get(0).getMaintenance().substring(1));
                if (this.service.sendMessage(remindsOneTime.get(0).getUserChatID(),
                        REMIND_MESSAGE + maintenance+".")) {
                int indexToDelete = Integer.parseInt(remindsOneTime.get(0).toString().
                        substring(remindsOneTime.get(0).toString().indexOf("=") + 1, remindsOneTime.get(0).toString().indexOf(",")));
                    RemindServiceImpl.newRemindService().deleteRemind(remindId, indexToDelete, getIdOfAllReminds());
                remindsOneTime.clear();
                }}
        }
        else if(!remindsSeveralTime.isEmpty() && remindsOneTime.isEmpty()){
        if(remindsSeveralTime.size() > 1) {
        maintenance = messageForSeveralRemindWithDailyRate((Remind[]) remindsSeveralTime.toArray());
        if(this.service.sendMessage(remindsSeveralTime.get(0).getUserChatID(), maintenance)){
            remindsSeveralTime.clear();
        }}
        else if(remindsSeveralTime.size() == 1){
            String maintenanceWithoutRegularMarker = String.format(Character.toLowerCase(
                    deleteRegularMarker(remindsSeveralTime.get(0)).charAt(0))+"%s",
                    deleteRegularMarker(remindsSeveralTime.get(0)).substring(1));
                if (this.service.sendMessage(remindsSeveralTime.get(0).getUserChatID(),
                        REMIND_MESSAGE + maintenanceWithoutRegularMarker+".")) {
                    RemindServiceImpl.newRemindService().updateDate(remindsSeveralTime.get(0),
                        nextDate(remindsSeveralTime.get(0).getRemindDate().split("")));
                    remindsSeveralTime.clear();
                }
            }}
        
        executeDate = RemindForDefPerson.dateToSend();
        if(isConditionsToSendToDefPerson(executeDate,currentDate())){
            RemindForDefPerson.send();
        }
    }

    private boolean isConditionsToSendOneTime(String executeDate, String currentDate, Remind remind) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 6 &&
                !isContainsDailySendMarker(remind.getMaintenance());
    }

    private boolean isConditionsToSendToDefPerson(String executeDate, String currentDate) {
        return (currentDate.equals(executeDate)) && (Integer.parseInt(currentTime()) >= 6);
    }

    private boolean isConditionsToSendDaily(String executeDate, String currentDate, Remind remind) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7 &&
                isContainsDailySendMarker(remind.getMaintenance());
            }

    private boolean isContainsDailySendMarker(String maintenance){
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }

    private boolean noDelete(int index) {
        return index == RemindForDefPerson.undeletedIndex;
    }


    private synchronized String lastCommand() {
        while (TelegramBot.commands.isEmpty()){
        try{
            wait();}
        catch (InterruptedException e){
            System.out.println("Something went wrong");}}
        notify();

        return TelegramBot.commands.get(TelegramBot.commands.size() - 1);
    }

    private void stop() {
        if (lastCommand().equals("/stop")) {
            stop = true;
        }
        if (lastCommand().equals("/restart")) stop = false;
    }


    private static String currentDate() {
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

    public static String currentTime() {
        String result;
        String[] tempTimes = Calendar.getInstance().toString().split(",");
        if (tempTimes[21].equals("AM_PM=1")) {
            result = String.valueOf(Integer.parseInt(tempTimes[22].
                    substring(tempTimes[22].indexOf("=") + 1)) + 12);
        } else {
            result = String.valueOf(Integer.parseInt(tempTimes[22].
                    substring(tempTimes[22].indexOf("=") + 1)));
        }
        return result;
    }

    public static String nextDate(String[] thisDate){
        String nextDate = String.format(thisDate[0]+"%d"+thisDate[2]+
                ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9], Integer.parseInt(thisDate[1])+1);

        if(nextDate.startsWith("0") && nextDate.indexOf(".")==3){
            nextDate = nextDate.substring(1);}

        if(nextDate.indexOf(".") == 3){
            nextDate = String.format("%d"+thisDate[2]+
                    ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                    thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9],
                    Integer.parseInt(thisDate[0]+thisDate[1])+1);
        }

        String lastDate = lastDayInMonth.get(nextDate.substring(nextDate.indexOf(".")+1,
                nextDate.lastIndexOf(".")));

        if((Integer.parseInt(nextDate.substring(0, nextDate.indexOf(".")))-1)
                == Integer.parseInt(lastDate.substring(0, lastDate.indexOf(".")))){
            nextDate = toNextMonth(nextDate);}

        return nextDate;}

    private static String deleteRegularMarker(Remind remind){
        return remind.getMaintenance().substring(remind.getMaintenance().indexOf(" ")+1);
        }

        public static String toNextMonth(String date){
        String[] currentDate = date.split("\\.");{
        currentDate[0] = "01";
        String day = currentDate[0];
        String month = "";
        if(currentDate[1].startsWith("0")){
            month += ("0")+(Integer.parseInt(currentDate[1].substring(1))+1);}
        else{
        month += (Integer.parseInt(currentDate[1])+1);}
        String year = currentDate[2];

        return String.format("%s.%s.%s", day, month, year);
            }
        }

    public synchronized boolean showRemindsByDate(String userChatId, String date) throws InterruptedException{
        List<Remind> reminds;
        while((reminds = RemindServiceImpl.
                newRemindService().getAllRemindsFromDB()).size()==0){
            wait();}
        notify();
        int index = 0, count = 0;
        service.sendMessage(userChatId, "Через пару секунд пришлю все напоминания...");
        Thread.sleep(2300);
        while (index != reminds.size()){
            Remind remind = reminds.get(index);
            if((remind.getUserChatID().equals(userChatId) && remind.getRemindDate().equals(date))
            &&!isContainsDailySendMarker(remind.getMaintenance())){
            service.sendMessage(userChatId, remind.getMaintenance());
                count ++;
            }
                index ++;
            }
        return count > 0;}

    private String messageForSeveralRemind(int[] arrayId, Remind[] reminds) throws InterruptedException{
        String messageToSend = "Позвольте напомнить, что вам нужно сделать следующее:\n";
        int[] numbers = new int[reminds.length];
        for(int i  = 1; i < numbers.length; i ++){ numbers[i] = i; }
        for(int i = 0; i < reminds.length; i++){
            messageToSend += numbers[i]+". "+reminds[i].getMaintenance()+"."+"\n";
        RemindServiceImpl.newRemindService().deleteRemind(Integer.parseInt(reminds[i].toString().
                substring(reminds[i].toString().indexOf("=") + 1, reminds[i].toString().indexOf(","))));
        arrayId = getIdOfAllReminds();}
        return messageToSend;
        }


    private String messageForSeveralRemindWithDailyRate(Remind[] reminds) throws InterruptedException{
        String messageToSend = "Позвольте напомнить, что вам нужно сделать следующее:\n";
        int[] numbers = new int[reminds.length];
        for(int i  = 1; i < numbers.length; i ++){ numbers[i] = i; }
        for(int i = 0; i < reminds.length; i++){
            messageToSend += numbers[i]+". "+deleteRegularMarker(reminds[i])+"."+"\n";
            RemindServiceImpl.newRemindService().updateDate(reminds[i],
                    nextDate(reminds[i].getRemindDate().split("")));}
        return messageToSend;
    }
}











