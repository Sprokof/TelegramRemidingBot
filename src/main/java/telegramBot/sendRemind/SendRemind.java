package telegramBot.sendRemind;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Remind;
import telegramBot.hidenPackage.RemindForDefPerson;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import java.io.IOException;
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
    @Getter
    private static final String REMIND_MESSAGE = "Позвольте напомнить, что вам нужно ";
    private final RemindForDefPerson remindForDefPerson;

    @Autowired
    public SendRemind(SendMessageService service) {
        this.service = (SendMessageServiceImpl) service;
        this.remindForDefPerson = new RemindForDefPerson(this);
    }

    private synchronized int[] getIdOfAllReminds() throws InterruptedException {
        List<Remind> reminds;
        while ((reminds = RemindServiceImpl.newRemindService().
                getAllRemindsFromDB()).size() == 0) {
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

    public void send() throws InterruptedException {
        int[] remindId = getIdOfAllReminds();
        List<Remind> remindsToExecute = null;
        List<Remind> reminds = new ArrayList<>();
        for (int index = 0; index < remindId.length; index++) {
            if (noDelete(remindId[index])) continue;
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindId[index]);
            if (remind.getRemindDate().equals(currentDate())) {
                if (changedRemind(remind, currentTime(), remindId[index])) {
                    reminds.add(remind);
                }
                if (alreadyAddedRemind(remind)) {
                    reminds.add(remind);
                }
            }
        }

        for (Remind remind : reminds) {
            while ((remindsToExecute = RemindServiceImpl.newRemindService().
                    getAllExecutingRemindsByChatId(remind.getChatIdToSend())).size() != 0) {
                while (true) {
                    List<Remind> once = sortReminds(remindsToExecute).get(0);
                    List<Remind> daily = sortReminds(remindsToExecute).get(1);

                    if (sendRemindsInOneDay(once)) {
                        break;
                    }

                    if (sendRemindWithDailyRate(daily)) {
                        break;
                    }
                }

            }

        }

            if (remindForDefPerson.dateToSend().equals(currentDate())) {
                changedRemind(remindForDefPerson.getRemind(),
                        currentTime(), RemindForDefPerson.undeletedIndex); }

            if (isConditionsToSendToDefPerson(remindForDefPerson.dateToSend(), currentDate(),
                    remindForDefPerson.getRemind())) {
                try {
                    remindForDefPerson.send(currentTime());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        deleteNotUpdatedRemind();
    }


    private List<List<Remind>> sortReminds(List<Remind> reminds) {
        List<List<Remind>> list = new ArrayList<>();
        if (reminds != null) {
            List<Remind> once = new ArrayList<>();
            List<Remind> daily = new ArrayList<>();
            for (Remind remind : reminds) {
                if (!isContainsDailySendMarker(remind.getMaintenance())) {
                    once.add(remind);
                } else if (isContainsDailySendMarker(remind.getMaintenance()))
                    daily.add(remind);
            }
            list.add(once);
            list.add(daily);
        }
        return list;
    }


    private boolean isConditionsToSendToDefPerson(String executeDate, String currentDate, Remind remind) {
        return (currentDate.equals(executeDate)) && (currentTime() >= 5) &&
                remind.getTimeToSend().equals("true");
    }


    private boolean isContainsDailySendMarker(String maintenance) {
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }

    private boolean noDelete(int index) {
        return index == RemindForDefPerson.undeletedIndex;
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

    public static int currentTime() {
        String hour;
        String[] tempTimes = Calendar.getInstance().toString().split(",");
        if (tempTimes[21].equals("AM_PM=1")) {
            hour = String.valueOf(Integer.parseInt(tempTimes[22].
                    substring(tempTimes[22].indexOf("=") + 1)) + 12);
        } else {
            hour = String.valueOf(Integer.parseInt(tempTimes[22].
                    substring(tempTimes[22].indexOf("=") + 1)));
        }
        return Integer.parseInt(hour);
    }

    public static String nextDate(String[] thisDate) {
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
        return remind.getMaintenance().substring(remind.getMaintenance().indexOf(" ") + 1);
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
        List<Remind> reminds;
        while ((reminds = RemindServiceImpl.
                newRemindService().getAllRemindsFromDB()).size() <= 1) {
            wait();
        }
        notify();

        int index = 0, count = 0;
        service.sendMessage(userChatId, "Через пару секунд пришлю все напоминания...");
        Thread.sleep(2300);
        while (index != reminds.size()) {
            Remind remind = reminds.get(index);
            if ((remind.getChatIdToSend().equals(userChatId) && remind.getRemindDate().equals(date))
                    && !isContainsDailySendMarker(remind.getMaintenance())) {
                service.sendMessage(userChatId, remind.getMaintenance());
                count++;
            }
            index++;
        }
        TelegramBot.setRun(true);
        return count > 0;
    }

    private String messageOnce(Remind[] reminds) {
        String messageToSend = "Позвольте напомнить, что вам нужно сделать следующее:\n";
        for (int i = 0; i < reminds.length; i++) {
            int num = (i + 1);

            messageToSend += num + ") " + reminds[i].getMaintenance() + "." + "\n";
            updateRemindFieldsToNextSendTime(reminds[i], reminds[i].getCountSendOfRemind() + 1);
        }

        for (int i = 0; i < reminds.length; i++) {
            if (reminds[i].getCountSendOfRemind() == 3) {
                int id = getIdOfRemind(reminds[i]);
                RemindServiceImpl.newRemindService().deleteRemind(id);
            }
        }
        return messageToSend;
    }


    private String messageForDaily(Remind[] reminds) {
        String messageToSend = "Позвольте напомнить, что вам нужно сделать следующее:\n";
        for (int i = 0; i < reminds.length; i++) {
            int num = (i + 1);
            String str = String.format(Character.
                    toUpperCase(deleteRegularMarker(reminds[i]).charAt(0)) + "%s", deleteRegularMarker(
                    reminds[i]).substring(1));

            messageToSend += num + ") " + str + "." + "\n";
            updateRemindFieldsToNextSendTime(reminds[i], reminds[i].getCountSendOfRemind() + 1);
        }


        for (int i = 0; i < reminds.length; i++) {
            if (reminds[i].getCountSendOfRemind() == 3) {
                String date = nextDate(reminds[i].getRemindDate().split(""));
                updateRemindFieldsToNextDay(reminds[i], date);
            }
        }
        return messageToSend;
    }

    public boolean changedRemind(Remind remind, int currentTime, int index) {
        boolean flag = false;
        if (remind.getTimeToSend().equals("false")) {
            if (((currentTime - remind.getLastSendHour()) >= 4) && (currentTime < 23)) {
                RemindServiceImpl.newRemindService().updateSendHourField(remind, currentTime);
                RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
                flag = true;
            }
        }
        if (currentTime >= 23 && (remind.getCountSendOfRemind() <= 3 && remind.getCountSendOfRemind() >= 1)
                || currentTime <= 3 && (remind.getCountSendOfRemind() <= 3 && remind.getCountSendOfRemind() >= 1)) {
            if (isContainsDailySendMarker(remind.getMaintenance()) || noDelete(index)) {
                String date = nextDate(remind.getRemindDate().split(""));
                updateRemindFieldsToNextDay(remind, date);
                flag = true;
            } else {
                RemindServiceImpl.newRemindService().deleteRemind(index);
                flag = true;
            }
        }
        return flag;
    }

    private void updateRemindFieldsToNextDay(Remind remind, String date) {
        RemindServiceImpl.newRemindService().updateRemindDateField(remind, date);
        RemindServiceImpl.newRemindService().updateCountSendField(remind, 0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, 0);
    }

    private void updateRemindFieldsToNextSendTime(Remind remind, int count) {
        RemindServiceImpl.newRemindService().updateCountSendField(remind, count);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, false);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, currentTime());
    }

    private void deleteNotUpdatedRemind() {
        List<Remind> reminds = RemindServiceImpl.newRemindService().getAllRemindsFromDB();
        reminds.forEach((r) -> {
            if (nextDate(r.getRemindDate().split("")).equals(currentDate())) {
                int id = getIdOfRemind(r);
                RemindServiceImpl.newRemindService().deleteRemind(id);
            }
        });

    }

    private boolean sendRemindWithDailyRate(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        Remind remind = reminds.get(0);

        if (reminds.size() > 1) {
            for (int i = 0; i < reminds.size(); i++) {
                String maintenance = messageForDaily(reminds.toArray(Remind[]::new));
                this.service.sendMessage(remind.getChatIdToSend(), maintenance);
                reminds.clear();
            }

        } else {
            String maintenanceWithoutRegularMarker = String.format(Character.toLowerCase(
                            deleteRegularMarker(remind).charAt(0)) + "%s",
                    deleteRegularMarker(remind).substring(1));
            if (this.service.sendMessage(remind.getChatIdToSend(),
                    REMIND_MESSAGE + maintenanceWithoutRegularMarker + ".")) {
                updateRemindFieldsToNextSendTime(remind, reminds.get(0).
                        getCountSendOfRemind() + 1);
                if (remind.getCountSendOfRemind() == 3) {
                    String date = nextDate(remind.getRemindDate().split(""));
                    updateRemindFieldsToNextDay(remind, date);
                    reminds.clear();
                }
            }
        }
        return true;

    }

    private boolean sendRemindsInOneDay(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        Remind remind = reminds.get(0);

        if (reminds.size() > 1) {
            for (int i = 0; i < reminds.size(); i++) {
                String maintenance = messageOnce(reminds.toArray(Remind[]::new));
                this.service.sendMessage(remind.getChatIdToSend(), maintenance);
                reminds.clear();
            }

        } else {
            String maintenance = (Character.toLowerCase(remind.getMaintenance().
                    charAt(0)) + remind.getMaintenance().substring(1));
            int indexOfDeleteRemind = getIdOfRemind(remind);
            if (this.service.sendMessage(remind.getChatIdToSend(),
                    REMIND_MESSAGE + maintenance + ".")) {
                updateRemindFieldsToNextSendTime(remind,
                        remind.getCountSendOfRemind() + 1);
                reminds.clear();
            }

            if (remind.getCountSendOfRemind() == 3) {
                RemindServiceImpl.newRemindService().deleteRemind(indexOfDeleteRemind);
            }
        }
            return true;

    }

    private int getIdOfRemind(Remind remind) {
        return Integer.parseInt(remind.toString().
                substring(remind.toString().indexOf("=") + 1,
                        remind.toString().indexOf(",")));
    }

    private boolean alreadyAddedRemind(Remind remind) {
        return remind.getCountSendOfRemind() == 0 &&
                remind.getTimeToSend().equals("true") && remind.getLastSendHour() == 0;
    }

}






















