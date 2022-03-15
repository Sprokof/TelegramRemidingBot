package telegramBot.sendRemind;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.service.*;

import java.util.*;

@Component
public class SendRemind {
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

    private SendMessageServiceImpl service;
    private DeleteMessageServiceImpl deleteService;
    @Getter
    private static final String REMIND_MESSAGE = "Позвольте напомнить, что вам следует ";
    private static final String SHOW_MESSAGE = "На эту дату есть следующие записи:\n";

    @Autowired
    public SendRemind(SendMessageService service, DeleteMessageServiceImpl deleteService) {
        this.service = (SendMessageServiceImpl) service;
        this.deleteService = deleteService;
    }

    private synchronized int[] getIdOfAllReminds() throws InterruptedException {
        List<Remind> reminds;
        while ((reminds = RemindServiceImpl.newRemindService().
                getAllRemindsFromDB()).isEmpty()) {
            wait();
        }
        notify();
        int[] ides = null;
        try {
            ides = new int[reminds.size()];
            Remind remind;
            for (int i = 0; i < ides.length; i++) {
                remind = reminds.get(i);
                ides[i] = getIdOfRemind(remind);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ides;
    }

    public void execute() throws InterruptedException {
        int[] remindId = getIdOfAllReminds();
        List<Remind> reminds = new ArrayList<>();
        for (int index = 0; index < remindId.length; index++) {
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindId[index]);
            if (remind.getRemindDate().equals(currentDate())) {
                if (changedRemind(remind, remindId[index]) || alreadyAddedRemind(remind)) {
                    reminds.add(remind);
                }
            }
        }

        reminds.forEach((r) -> {
            List<Remind> waitsExecuteReminds = null;
            while (!(waitsExecuteReminds = RemindServiceImpl.newRemindService().
                    getAllExecutingRemindsByChatId(r.getDetails().getChatIdToSend())).isEmpty()) {
                while (true) {
                    if (send(waitsExecuteReminds)) {
                        break;
                    }
                }

            }
        });

        deleteNotUpdatedRemind();
    }



    private boolean isContainsDailySendMarker(String maintenance) {
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
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

    public static String currentTime() {
        String[] calendarsParams = Calendar.getInstance().toString().split(",");
        String hour; String minutes = calendarsParams[24].
                substring(calendarsParams[24].indexOf("=")+1);

        if (calendarsParams[21].equals("AM_PM=1")) {
            hour = String.valueOf(Integer.parseInt(calendarsParams[22].
                    substring(calendarsParams[22].indexOf("=") + 1)) + 12);
        } else {
            hour = String.valueOf(Integer.parseInt(calendarsParams[22].
                    substring(calendarsParams[22].indexOf("=") + 1)));
        }
        if(minutes.length() == 1) minutes = "0"+minutes;
        return String.format("%s:%s", hour, minutes);
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
            nextDate = toNextMonth(nextDate);
        }

        return nextDate;
    }

    private static String deleteRegularMarker(Remind remind) {
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getKey());
        return decrypt.substring(decrypt.indexOf(" ") + 1);
    }

    public static String toNextMonth(String date) {
        String[] currentDate = date.split("\\.");
        {
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
    }

    public synchronized boolean showRemindsByDate(String userChatId, String date) throws InterruptedException {
        int[] remindsId;
        while ((remindsId = getIdOfAllReminds()).length == 0 ) {
            wait();
        }

        int index = 0, count = 0;
        service.sendMessage(userChatId, "Через пару секунд пришлю напоминания на " + dayAndMonth(date));
        Thread.sleep(4700);
        String messageToSend = SHOW_MESSAGE;
        while (index != remindsId.length) {
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindsId[index]);
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());
            if ((remind.getDetails().getChatIdToSend().equals(userChatId) &&
                    remind.getRemindDate().equals(date.replaceAll("\\p{P}", "\\.")))
                    && !isContainsDailySendMarker(decrypt)) {
                messageToSend = messageToSend + (index) + ") " + decrypt+"\n";
                count++;
            }
            index++;
        }
                if(count != 0) service.sendMessage(userChatId, messageToSend);
        return count > 0;
    }

    private String messageForLonelyRemind(Remind remind) {
        String messageToSend = REMIND_MESSAGE;
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getKey());

        if(isContainsDailySendMarker(decrypt)){
            messageToSend = messageToSend + deleteRegularMarker(remind);
        }
        else{
            messageToSend = (messageToSend +
                String.valueOf(decrypt.
                        charAt(0)).toLowerCase(Locale.ROOT)+ decrypt.substring(1));
        }

        updateRemindFieldsToNextSendTime(remind, remind.getDetails().getCountSendOfRemind()+1);
            if(remind.getDetails().getCountSendOfRemind() == 3){
                if(isContainsDailySendMarker(decrypt)){
                    String date = nextDate(remind.getRemindDate());
                    updateRemindFieldsToNextDay(remind, date);
                }
                else {
                    int id = getIdOfRemind(remind);
                    RemindServiceImpl.newRemindService().deleteRemind(id);
                }

            }
        return messageToSend;
    }


    private String messageForAggregateRemind(Remind[] reminds) {
        String messageToSend = REMIND_MESSAGE + "сделать следующее:\n";
        String string;
        for (int i = 0; i < reminds.length; i++) {
            Remind remind = reminds[i];
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());
            int num = (i + 1);
            if(isContainsDailySendMarker(decrypt)){
            string  = String.format(Character.
                    toUpperCase(deleteRegularMarker(remind).charAt(0)) + "%s", deleteRegularMarker(
                    remind).substring(1));
            }
           else {
               string = String.valueOf(decrypt.
                    charAt(0)).toLowerCase(Locale.ROOT)+ decrypt.substring(1);
           }

            messageToSend = messageToSend + num + ") " + string + "." + "\n";
            updateRemindFieldsToNextSendTime(reminds[i],
                    reminds[i].getDetails().getCountSendOfRemind() + 1);
        }


        for (int i = 0; i < reminds.length; i++) {
            Remind remind = reminds[i];

            String decrypted = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());

            if ((isContainsDailySendMarker(decrypted))) {
                if (remind.getDetails().getCountSendOfRemind() == 3) {
                    String date = nextDate(reminds[i].getRemindDate());
                    updateRemindFieldsToNextDay(reminds[i], date);
                }
            } else {
                if (remind.getDetails().getCountSendOfRemind() == 3) {
                    int id = getIdOfRemind(remind);
                    RemindServiceImpl.newRemindService().deleteRemind(id);
                }
            }
        }

        return messageToSend;
    }


    public boolean changedRemind(Remind remind, int index) {
        boolean flag = false;
        double time = toDoubleTime();
        if (remind.getDetails().getTimeToSend().equals("false")) {
            if ((timeDifference(remind.getDetails().getLastSendTime()) >= 4.01) && (time < 23)) {
                RemindServiceImpl.newRemindService().updateSendHourField(remind, currentTime());
                RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
                flag = true;
            }
        }
        if (time >= 23 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)
                || time <= 3 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)) {

            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());

            if (isContainsDailySendMarker(decrypt)) {
                String date = nextDate(remind.getRemindDate());
                updateRemindFieldsToNextDay(remind, date);
            } else {
                RemindServiceImpl.newRemindService().deleteRemind(index);
            }
        }
        return flag;
    }

    public void updateRemindFieldsToNextDay(Remind remind, String date) {
        RemindServiceImpl.newRemindService().updateRemindDateField(remind, date);
        RemindServiceImpl.newRemindService().updateCountSendField(remind, 0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, "-");
    }

    public void updateRemindFieldsToNextSendTime(Remind remind, int count) {
        RemindServiceImpl.newRemindService().updateCountSendField(remind, count);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, false);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, currentTime());
    }

    private void deleteNotUpdatedRemind() {
        List<Remind> reminds = RemindServiceImpl.newRemindService().getAllRemindsFromDB();
        reminds.forEach((r) -> {
            if (nextDate(r.getRemindDate()).equals(currentDate())) {
                int id = getIdOfRemind(r);
                RemindServiceImpl.newRemindService().deleteRemind(id);
            }
        });

    }

    private boolean send(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        List<Message> messages = MessageServiceImpl.newMessageService().getAllMessages();
        Remind remind = reminds.get(0);
        String maintenance;
        String chatId = remind.getDetails().getChatIdToSend();

        if(!messages.isEmpty())
            this.deleteService.deleteMessage(chatId, SendMessageServiceImpl.getMessageId());
        MessageServiceImpl.newMessageService().
                deleteMessageByMessageId(SendMessageServiceImpl.getMessageId());

        if (reminds.size() > 1) {
               maintenance  = messageForAggregateRemind(reminds.toArray(Remind[]::new));
           reminds.clear();
                this.service.sendMessage(chatId, maintenance);
        }
        else {
                maintenance = messageForLonelyRemind(remind);
           reminds.clear();
                this.service.sendMessage(chatId, maintenance);
                }

        MessageServiceImpl.newMessageService().save(new Message(chatId, SendMessageServiceImpl.
                getMessageId()));

        return true; }


     private int getIdOfRemind(Remind remind) {
        return Integer.parseInt(remind.toString().
                substring(remind.toString().indexOf("=") + 1,
                        remind.toString().indexOf(",")));
    }

    private boolean alreadyAddedRemind(Remind remind) {
        Remind remindRecipient = null;
        if(remind.getDetails().getLastSendTime().equals("-")){
            String chatId = remind.getDetails().getChatIdToSend();
                    if(!RemindServiceImpl.newRemindService().getAllNotExecutingRemindsByChatId(chatId).isEmpty()){
                        remindRecipient = RemindServiceImpl.newRemindService().
                                getAllNotExecutingRemindsByChatId(chatId).get(0);}
            if (remindRecipient != null) {
            RemindServiceImpl.newRemindService().updateSendHourField(remind, remindRecipient.getDetails().getLastSendTime());}
            return true;
        }
        return false;
        }


    private String detachMonthFromInputDate(String date){
        String intView = date.split("\\p{P}")[1];
        String month = null;
        switch (intView){
            case "01": month = "январь"; break;
            case "02": month = "февраль"; break;
            case "03": month = "март"; break;
            case "04": month = "апрель"; break;
            case "05": month = "май"; break;
            case "06": month = "июнь"; break;
            case "07": month = "июль"; break;
            case "08": month = "август"; break;
            case "09": month = "сентябрь"; break;
            case "10": month = "октябрь"; break;
            case "11": month = "ноябрь"; break;
            case "12": month = "декабрь"; break;
        }
        return month;

        }


    private String dayAndMonth(String date){
        String month = detachMonthFromInputDate(date);
        if(month.charAt(month.length()-1) == 'ь' || month.equals("май")){
            month = (month.substring(0, month.length()-1) + "я");
        }
        else{
        month = (month.substring(0, month.length()) + "а");}

        String day = date.split("\\p{P}")[0];
        if(day.startsWith("0")){
            day = String.valueOf(day.charAt(1)); }

        return String.format("%s %s", day, month);
    }

    public static double timeDifference(String lastSendTime){
        double current = Double.parseDouble(currentTime().replace(':', '.'));
        double last = Double.parseDouble(lastSendTime.replace(':', '.'));
    return current - last;
    }

    public static double toDoubleTime(){
        return Double.parseDouble(SendRemind.currentTime().replace(':', '.'));
    }

}























