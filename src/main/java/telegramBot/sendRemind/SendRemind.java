package telegramBot.sendRemind;

import org.hibernate.Session;
import telegramBot.bot.TelegramBot;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.hidenPackage.RemindForTanya;
import telegramBot.service.SendMessageServiceImpl;

import java.util.*;

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

    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    private static boolean stop = false;
    private static final String REMIND_MESSAGE = "Позвольте напомнить, что вам нужно ";

    public void executeRemindMessage() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
        try{
            findAndSendRemind();
            }
        catch (InterruptedException e){e.printStackTrace();
        }}};
        timer.scheduleAtFixedRate(task, 2500, 60000);
    }


    private synchronized int[] getIdOfRemind() throws InterruptedException {
        while (getRemindFromDB().size() <= 1) {
            wait();
        }
        notify();
        int[] ides = null;
        try {
            ides = new int[getRemindFromDB().size()];
            String id;
            Remind notice;
            for (int i = 0; i < ides.length; i++) {
                notice = getRemindFromDB().get(i);
                id = notice.toString().
                        substring(notice.toString().indexOf("=") + 1, notice.toString().indexOf(","));
                ides[i] = Integer.parseInt(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ides;
    }

    private void findAndSendRemind() throws InterruptedException {
        int[] remindId = getIdOfRemind();
        String executeDate;
        stop();
        for (int index = 0; index < remindId.length; index++) {
            if (noDelete(remindId[index])) continue;
            Remind remind = new RemindDAOImpl().getObjectByID(remindId[index]);
            executeDate = remind.getRemindDate();
            if (isConditionsToSendOneTime(executeDate, currentDate(), remind)) {
                String maintenance = (Character.toLowerCase(remind.getMaintenance().
                        charAt(0))+remind.getMaintenance().substring(1));
                if (sendMessageService.sendMessage(remind.getUserChatID(),
                        REMIND_MESSAGE + maintenance+".")) {
                    deleteRemind(remindId, index);
                }
            }
        else if(isConditionsToSendDaily(executeDate, currentDate(), remind)){
                if (sendMessageService.sendMessage(remind.getUserChatID(),
                        REMIND_MESSAGE + deleteRegularMarker(remind)+".")) {
                updateDate(remind);
                }
            }
        }
        executeDate = RemindForTanya.date();
        if (isConditionsToSendToTanya(executeDate, currentDate())) {
            RemindForTanya.send();
        }
    }

    private boolean isConditionsToSendOneTime(String executeDate, String currentDate, Remind notice) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7 &&
                !containsDailySendMarker(notice.getMaintenance());
    }

    private boolean isConditionsToSendToTanya(String executeDate, String currentDate) {
        return (currentDate.equals(executeDate)) &&
                (Integer.parseInt(currentTime()) >= 6 && Integer.parseInt(currentTime()) <= 22);
    }

    private boolean isConditionsToSendDaily(String executeDate, String currentDate, Remind remind) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7 &&
                containsDailySendMarker(remind.getMaintenance());
            }

    private boolean containsDailySendMarker(String maintenance){
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }

    private boolean noDelete(int index) {
        return index == RemindForTanya.undeletedNoticeIndex;
    }


    private String lastCommand() {
        return TelegramBot.commands.get(TelegramBot.commands.size() - 1);
    }

    private void stop() {
        if (lastCommand().equals("/stop")) {
            stop = true;
        }
        if (lastCommand().equals("/restart")) stop = false;
    }

    private void deleteRemind(int[] noticeId, int index) throws InterruptedException {
        try {
            new RemindDAOImpl().deleteByID(noticeId[index]);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println("delete sent notice");
            wait(1350);
            notify();
            noticeId = getIdOfRemind();
        }
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

    private List<Remind> getRemindFromDB() {
        Session session;
        RemindDAOImpl remindDAO = new RemindDAOImpl();
        List<?> temp = null;
        try {
            session = remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            temp = session.createSQLQuery("SELECT id," +
                    "MAINTENANCE, REMIND_DATE, USER_CHAT_ID from REMINDERS").
                    addEntity(Remind.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            remindDAO.getSessionFactory().close();
        }
        List<Remind> reminds = new ArrayList<>();
        for (Iterator<?> it = temp.iterator(); it.hasNext();) {
            reminds.add((Remind) it.next());
        }
        return reminds;
    }

    private static String currentTime() {
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
        String date = String.format(thisDate[0]+"%d"+thisDate[2]+
                ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9], Integer.parseInt(thisDate[1])+1);

        if(date.startsWith("0") && date.indexOf(".")==3){ date = date.substring(1);}
        if(date.indexOf(".") == 3){
            date = String.format("%d"+thisDate[2]+
                    ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                    thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9],
                    Integer.parseInt(thisDate[0]+thisDate[1])+1);
        }

        String lastDate = lastDayInMonth.get(date.substring(date.indexOf(".")+1, date.lastIndexOf(".")));

        if((Integer.parseInt(date.substring(0, date.indexOf("."))))
                == Integer.parseInt(lastDate.substring(0, lastDate.indexOf(".")))){
            String temp = date.substring(date.lastIndexOf("."));
            date = lastDate + temp;}

        return date;}

    private static void updateDate(Remind remind){
        remind.setRemindDate((nextDate(remind.getRemindDate().split(""))));
        new RemindDAOImpl().update(remind);
    }

    private static String deleteRegularMarker(Remind remind){
        String maintenance = remind.getMaintenance().substring(remind.getMaintenance().indexOf(" ")+1);
        char firstLetter = Character.toLowerCase(maintenance.charAt(0));
        return String.format(firstLetter+"%s", maintenance.substring(1));
        }
    }










