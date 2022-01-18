package telegramBot.hidenPackage;

import telegramBot.bot.TelegramBot;
import telegramBot.dao.NoticeDAOImpl;
import telegramBot.entity.Notice;
import telegramBot.notification.SendNotice;
import telegramBot.service.SendMessageServiceImpl;


public class NoticeForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static final int undeletedNoticeIndex = 1;

    public static void send(){
        Notice notice = new NoticeDAOImpl().getObjectByID(undeletedNoticeIndex);
        sendMessageService.sendMessage(notice.getUserChatID(),
                notice.getMaintenance());
            String[] thisDate = notice.getNoticeDate().split("");
            notice.setNoticeDate(SendNotice.nextDate(thisDate));
            new NoticeDAOImpl().update(notice);}

    public static String date(){
    return new NoticeDAOImpl().getObjectByID(undeletedNoticeIndex).getNoticeDate();}



    }


