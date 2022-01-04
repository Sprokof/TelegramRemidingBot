package telegramBot.hiden;

import telegramBot.bot.TelegramBot;
import telegramBot.entity.Notice;
import telegramBot.service.SendMessageServiceImpl;

public class NoticeForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static Notice notice = new Notice("838370915", "Татьяна, " +
            "не устаю повторять, что вы сама прекрасная", "05.01.2022");

    public static void send(){
        if(sendMessageService.sendMessage(notice.getUserChatID(),
                notice.getMaintenance())){
            String[] thisDate = notice.getNoticeDate().split("");
            String nextDate = String.format(thisDate[0]+"%d"+thisDate[2]+
                    ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                    thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9], Integer.parseInt(thisDate[1])+1);
            notice.setNoticeDate(nextDate);
        }}
}
