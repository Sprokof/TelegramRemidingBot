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
        Remind remind = RemindServiceImpl.remindService().getRemindById(undeletedNoticeIndex);
        sendMessageService.sendMessage(remind.getUserChatID(),
            remind.getMaintenance().substring(5));
        String c = String.format("(c.%d)",
                Integer.parseInt(String.valueOf(remind.getMaintenance().charAt(3)))+1);
        String toUpdate = c+(remind.getMaintenance().substring(5));
        RemindServiceImpl.remindService().updateMaintenance(remind, toUpdate);
        if(c.equals("(c.2)")||SendRemind.currentTime().equals("23")){
            RemindServiceImpl.remindService().updateDate(remind,
                    SendRemind.nextDate(remind.getRemindDate().split("")));
            c = String.format("(c.%d)",
                    Integer.parseInt(String.valueOf(remind.getMaintenance().charAt(3)))-2);
            toUpdate = c+(remind.getMaintenance().substring(5));
            RemindServiceImpl.remindService().updateMaintenance(remind, toUpdate);



    }}

    public static String dateToSend(){
    return new RemindServiceImpl(new RemindDAOImpl()).getRemindById(undeletedNoticeIndex).getRemindDate();}



    }


