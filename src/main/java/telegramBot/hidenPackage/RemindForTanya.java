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
    public static int countSend = 0;

    public static void send(){
        Remind remind = new RemindServiceImpl(new RemindDAOImpl()).getRemindById((undeletedNoticeIndex));
        if(sendMessageService.sendMessage(remind.getUserChatID(),
                remind.getMaintenance())) countSend ++ ;

            String[] thisDate = remind.getRemindDate().split("");
            if(countSend == 2||(SendRemind.currentTime().equals("23"))){
            new RemindServiceImpl(new RemindDAOImpl()).updateDate(remind,
                    SendRemind.nextDate(remind.getRemindDate().split("")));
            countSend = 0;}
    }

    public static String dateToSend(){
    return new RemindServiceImpl(new RemindDAOImpl()).getRemindById(undeletedNoticeIndex).getRemindDate();}



    }


