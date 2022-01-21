package telegramBot.hidenPackage;

import telegramBot.bot.TelegramBot;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.SendMessageServiceImpl;


public class RemindForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static final int undeletedNoticeIndex = 1;

    public static void send(){
        Remind remind = new RemindDAOImpl().getObjectByID(undeletedNoticeIndex);
        sendMessageService.sendMessage(remind.getUserChatID(),
                remind.getMaintenance());
            String[] thisDate = remind.getRemindDate().split("");
            remind.setRemindDate((SendRemind.nextDate(thisDate)));
            new RemindDAOImpl().update(remind);}

    public static String date(){
    return new RemindDAOImpl().getObjectByID(undeletedNoticeIndex).getRemindDate();}



    }


