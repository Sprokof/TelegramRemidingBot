package telegramBot.hidenPackage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.bot.TelegramBot;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;


public class RemindForDefPerson {
    private  static final SendMessageServiceImpl sendMessageService =
            new SendMessageServiceImpl(new TelegramBot());

    private SendRemind sendRemind;

    @Getter
    private Remind remind;

    public static final int undeletedIndex = 1;

    @Autowired
    public RemindForDefPerson(SendRemind sendRemind){
        this.sendRemind = sendRemind;
        this.remind = RemindServiceImpl.newRemindService().getRemindById(undeletedIndex);

    }


    public void send(){

        int count = 0;
        if(sendMessageService.sendMessage(this.remind.getUserChatID(), this.remind.getMaintenance())){
            count = this.remind.getCountSend();
            RemindServiceImpl.newRemindService().updateCountSendField(this.remind,count+1);
            RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind,false);
        }

        if(count == 3){
        RemindServiceImpl.newRemindService().updateRemindDateField(this.remind,
                SendRemind.nextDate(this.remind.getRemindDate().split("")));
        RemindServiceImpl.newRemindService().updateCountSendField(this.remind,0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind, true);}
    }


    public String dateToSend(){
    return RemindServiceImpl.newRemindService().getRemindById(undeletedIndex).getRemindDate();}
    }


