package telegramBot.hidenPackage;

import telegramBot.bot.TelegramBot;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageServiceImpl;


public class RemindForDefPerson {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static final int undeletedIndex = 1;
    private static int timeToSend = 0;

    public static void send(){
        Remind remind = RemindServiceImpl.newRemindService().getRemindById(undeletedIndex);

        sendMessageService.sendMessage(remind.getUserChatID(),remind.getMaintenance());
        RemindServiceImpl.newRemindService().updateDate(remind,
                SendRemind.nextDate(remind.getRemindDate().split("")));
    }




    public static String dateToSend(){
    return new RemindServiceImpl(new RemindDAOImpl()).getRemindById(undeletedIndex).
            getRemindDate();}
    }


