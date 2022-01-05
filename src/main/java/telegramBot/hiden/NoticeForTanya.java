package telegramBot.hiden;

import telegramBot.bot.TelegramBot;
import telegramBot.entity.Notice;
import telegramBot.service.SendMessageServiceImpl;

import java.io.*;

public class NoticeForTanya {
    private static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    public static String[] message = messageFromFile().split("\\/");

    public static Notice notice = new Notice(message[0], message[1], message[2]);

    public static boolean send(){
        if(sendMessageService.sendMessage(notice.getUserChatID(),
                notice.getMaintenance())){
            String[] thisDate = notice.getNoticeDate().split("");
            String nextDate = String.format(thisDate[0]+"%d"+thisDate[2]+
                    ""+thisDate[3]+""+thisDate[4]+""+thisDate[5]+""+
                    thisDate[6]+""+thisDate[7]+""+thisDate[8]+""+thisDate[9], Integer.parseInt(thisDate[1])+1);
            notice.setNoticeDate(nextDate);
            return true;}
    return false;}


    private static String messageFromFile(){
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/user/Desktop/message.txt"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; }
}
