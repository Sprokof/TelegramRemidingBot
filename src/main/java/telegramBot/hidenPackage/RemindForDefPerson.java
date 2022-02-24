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


    public void send(int currentTime) throws IOException{
        RemindServiceImpl.newRemindService().
                updateMaintenanceField(this.remind, getMaintenanceFromFile());

        if(sendMessageService.sendMessage(this.remind.getChatIdToSend(), this.remind.getMaintenance())){
            count = this.remind.getCountSendOfRemind();
            RemindServiceImpl.newRemindService().updateSendHourField(this.remind, currentTime);
            RemindServiceImpl.newRemindService().updateCountSendField(this.remind,count+1);
            RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind,false);
        }

        if((count = this.remind.getCountSendOfRemind()) == 3){
        RemindServiceImpl.newRemindService().updateRemindDateField(this.remind,
                SendRemind.nextDate(this.remind.getRemindDate().split("")));
        RemindServiceImpl.newRemindService().updateCountSendField(this.remind,0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind, true);
        count = 0;}
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



