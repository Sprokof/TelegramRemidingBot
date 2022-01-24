package telegramBot.hidenPackage;

import telegramBot.bot.TelegramBot;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageServiceImpl;


public class RemindForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static final int undeletedNoticeIndex = 1;

    public static void send(){
        Remind remind = new RemindServiceImpl(new RemindDAOImpl()).getRemindById((undeletedNoticeIndex));
        sendMessageService.sendMessage(remind.getUserChatID(),
                remind.getMaintenance());
            String[] thisDate = remind.getRemindDate().split("");
            new RemindServiceImpl(new RemindDAOImpl()).updateDate(remind,
                    SendRemind.nextDate(remind.getRemindDate().split("")));
    }

    public static String date(){
    return new RemindServiceImpl(new RemindDAOImpl()).getRemindById(undeletedNoticeIndex).getRemindDate();}



    }


