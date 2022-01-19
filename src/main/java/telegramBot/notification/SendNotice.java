package telegramBot.notification;

import org.hibernate.Session;
import telegramBot.bot.TelegramBot;
import telegramBot.dao.NoticeDAOImpl;
import telegramBot.entity.Notice;
import telegramBot.hidenPackage.NoticeForTanya;
import telegramBot.myCollectionFramework.MyMap;
import telegramBot.service.SendMessageServiceImpl;

import java.util.*;

public class SendNotice {
   //public static final HashMap<String, String> lastDayInMonth = new HashMap<>();
   public static final MyMap<String, String> lastDayInMonth = new MyMap<>();

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

    public void executeNoticeAtDate() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    executeNotice();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 2500, 60000);
    }


    private synchronized int[] getIdOfNotice() throws InterruptedException {
        while (getNoticeFromDB().size() <= 1) {
            wait();
        }
        notify();
        int[] ides = null;
        try {
            ides = new int[getNoticeFromDB().size()];
            String id;
            Notice notice;
            for (int i = 0; i < ides.length; i++) {
                notice = getNoticeFromDB().get(i);
                id = notice.toString().
                        substring(notice.toString().indexOf("=") + 1, notice.toString().indexOf(","));
                ides[i] = Integer.parseInt(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ides;
    }

    private synchronized void executeNotice() throws InterruptedException {
        int[] noticeId = getIdOfNotice();
        String executeDate;
        stop();
        for (int index = 0; index < noticeId.length; index++) {
            if (noDelete(noticeId[index])) continue;
            Notice notice = new NoticeDAOImpl().getObjectByID(noticeId[index]);
            executeDate = notice.getNoticeDate();
            if (isConditionsToSendOneTime(executeDate, currentDate(), notice)) {
                if (sendMessageService.sendMessage(notice.getUserChatID(),
                        "Напоминание :" + " '" + notice.getMaintenance() + "'")) {
                    deleteNotice(noticeId, index);
                }
            }
        else if(isConditionsToSendDaily(executeDate, currentDate(), notice)){
                if (sendMessageService.sendMessage(notice.getUserChatID(),
                        "Напоминание :" + " '" + deleteRegularMarker(notice) + "'")) {
                updateDate(notice);
                }
            }
        }
        executeDate = NoticeForTanya.date();
        if (isConditionsToSendToTanya(executeDate, currentDate())) {
            NoticeForTanya.send();
        }
    }

    private boolean isConditionsToSendOneTime(String executeDate, String currentDate, Notice notice) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7 &&
                !containsDailySendMarker(notice.getMaintenance());
    }

    private boolean isConditionsToSendToTanya(String executeDate, String currentDate) {
        return (currentDate.equals(executeDate)) &&
                (Integer.parseInt(currentTime()) >= 6 && Integer.parseInt(currentTime()) <= 22);
    }

    private boolean isConditionsToSendDaily(String executeDate, String currentDate, Notice notice) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7 &&
                containsDailySendMarker(notice.getMaintenance());
            }

    private boolean containsDailySendMarker(String maintenance){
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }

    private boolean noDelete(int index) {
        return index == NoticeForTanya.undeletedNoticeIndex;
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

    private void deleteNotice(int[] noticeId, int index) throws InterruptedException {
        try {
            new NoticeDAOImpl().deleteByID(noticeId[index]);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println("delete sent notice");
            wait(1350);
            notify();
            noticeId = getIdOfNotice();
        }
    }

    private static String currentDate() {
        String[] tempDates = Calendar.getInstance().toString().split(",");
        String day = tempDates[17].substring(tempDates[17].indexOf("=") + 1);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String mouth = String.valueOf(Integer.parseInt(tempDates[14].substring(tempDates[14].indexOf("=") + 1)) + 1);
        if (mouth.length() == 1) {
            mouth = "0" + mouth;
        }
        String year = tempDates[13].substring(tempDates[13].indexOf("=") + 1);
        return String.format("%s.%s.%s", day, mouth, year);
    }

    private List<Notice> getNoticeFromDB() {
        Session session;
        NoticeDAOImpl noticeDAO = new NoticeDAOImpl();
        List<?> temp = null;
        try {
            session = noticeDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            temp = session.createSQLQuery("SELECT id," +
                    "MAINTENANCE, NOTICE_DATE, USER_CHAT_ID from NOTIFICATIONS").
                    addEntity(Notice.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            noticeDAO.getSessionFactory().close();
        }
        List<Notice> notices = new ArrayList<>();
        for (Iterator<?> it = temp.iterator(); it.hasNext();) {
            notices.add((Notice) it.next());
        }
        return notices;
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

        String lastDate = lastDayInMonth.getValue(date.substring(date.indexOf(".")+1, date.lastIndexOf(".")));

        if((Integer.parseInt(date.substring(0, date.indexOf("."))))
                == Integer.parseInt(lastDate.substring(0, lastDate.indexOf(".")))){
            String temp = date.substring(date.lastIndexOf("."));
            date = lastDate + temp;}

        return date;}

    private static void updateDate(Notice notice){
        notice.setNoticeDate(nextDate(notice.getNoticeDate().split("")));
        new NoticeDAOImpl().update(notice);
    }

    private static String deleteRegularMarker(Notice notice){
        String maintenance = notice.getMaintenance().substring(notice.getMaintenance().indexOf(" ")+1);
        char firstLetter = Character.toUpperCase(maintenance.charAt(0));
        return String.format(firstLetter+"%s", maintenance.substring(1));
        }
    }










