package telegramBot.notification;

import org.hibernate.Session;
import telegramBot.bot.TelegramBot;
import telegramBot.dao.NoticeDAOImpl;
import telegramBot.entity.Notice;
import telegramBot.hidenPackage.NoticeForTanya;
import telegramBot.service.SendMessageServiceImpl;

import java.util.*;

public class SendNotice {
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
            if (isConditionsToSend(executeDate, currentDate())) {
                if (sendMessageService.sendMessage(notice.getUserChatID(),
                        "Напоминание :" + " '" + notice.getMaintenance() + "'")) {
                    deleteNotice(noticeId, index);
                }
            }
        }
        executeDate = NoticeForTanya.date();
        if (isConditionsToSendToTanya(executeDate, currentDate())) {
            NoticeForTanya.send();
        }
    }

    private boolean isConditionsToSend(String executeDate, String currentDate) {
        return executeDate.replaceAll("\\p{P}", "\\.").equals(currentDate)
                && !stop && Integer.parseInt(currentTime()) >= 7;
    }

    private boolean isConditionsToSendToTanya(String executeDate, String currentDate) {
        return (currentDate.equals(executeDate)) &&
                (Integer.parseInt(currentTime()) >= 6 && Integer.parseInt(currentTime()) <= 22);
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

    private synchronized List<Notice> getNoticeFromDB() {
        Session session;
        NoticeDAOImpl noticeDAO = new NoticeDAOImpl();
        List<?> temp = null;
        try {
            session = noticeDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            temp = session.createSQLQuery("SELECT id," +
                    "MAINTENANCE, NOTICEDATE, USERID from NOTIFICATIONS").
                    addEntity(Notice.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            noticeDAO.getSessionFactory().close();
        }
        List<Notice> notices = new ArrayList<>();
        for (Iterator<?> it = temp.iterator(); it.hasNext(); ) {
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

}









