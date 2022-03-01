package telegramBot.hidenPackage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageServiceImpl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RemindForDefPerson {
    private  static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    private SendRemind sendRemind;

    @Getter
    private Remind remind;

    public static final int undeletedIndex = 1;
    private static int count = 0;

    @Autowired
    public RemindForDefPerson(SendRemind sendRemind){
        this.sendRemind = sendRemind;
        this.remind = RemindServiceImpl.newRemindService().getRemindById(undeletedIndex);

    }

    public void send() throws IOException{
        RemindServiceImpl.newRemindService().
                updateMaintenanceField(this.remind, getMaintenanceFromFile());

        if(sendMessageService.sendMessage(this.remind.getDetails().getChatIdToSend(), this.remind.getMaintenance())){
            count = this.remind.getDetails().getCountSendOfRemind();
            this.sendRemind.updateRemindFieldsToNextSendTime(remind, remind.getDetails().getCountSendOfRemind()+1);
        }

        if((count = this.remind.getDetails().getCountSendOfRemind()) == 3){
            String date = SendRemind.nextDate(remind.getRemindDate().split(""));
            this.sendRemind.updateRemindFieldsToNextDay(remind, date);
        count = 0;
        }
    }


    public String dateToSend(){
    return RemindServiceImpl.newRemindService().getRemindById(undeletedIndex).getRemindDate();}


    private String getMaintenanceFromFile() throws IOException{
    BufferedReader bf = new BufferedReader(new InputStreamReader(
                new FileInputStream("C:/Users/user/Desktop/version.txt")));
    String[] strings = new String[3];
    String m;
    int i = 0;
    while((m = bf.readLine()) != null){
        strings[i] = m;
        i ++;}

    double index = (Math.random()*2);
    return strings[(int) index];
    }
    }



