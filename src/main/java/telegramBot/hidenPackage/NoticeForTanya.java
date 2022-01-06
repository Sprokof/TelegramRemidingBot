package telegramBot.hidenPackage;

import telegramBot.bot.TelegramBot;
import telegramBot.dao.NoticeDAOImpl;
import telegramBot.entity.Notice;
import telegramBot.service.SendMessageServiceImpl;

public class NoticeForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static final int undeletedNoticeIndex = 2;

    public static boolean send(){
        Notice notice = new NoticeDAOImpl().getObjectByID(undeletedNoticeIndex);
        if(sendMessageService.sendMessage(notice.getUserChatID(),
                notice.getMaintenance())){
            String[] thisDate = notice.getNoticeDate().split("");
            String nextDate = String.format(thisDate[0]+"%d"+thisDate[2]+
                    ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                    thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9], Integer.parseInt(thisDate[1])+1);
            notice.setNoticeDate(nextDate);
            new NoticeDAOImpl().update(notice);
            return true;}
    return false;}

    public static String date(){
    return new NoticeDAOImpl().getObjectByID(undeletedNoticeIndex).getNoticeDate();
    }

}
