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
    private static int count = 0;

    @Autowired
    public RemindForDefPerson(SendRemind sendRemind){
        this.sendRemind = sendRemind;
        this.remind = RemindServiceImpl.newRemindService().getRemindById(undeletedIndex);

    }


    public void send(){

        if(sendMessageService.sendMessage(this.remind.getUserChatID(), this.remind.getMaintenance())){
            count = this.remind.getCountSend();
            RemindServiceImpl.newRemindService().updateCountSendField(this.remind,count++);
            RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind,false);
        }

        if((count = remind.getCountSend()) == 3){
        RemindServiceImpl.newRemindService().updateRemindDateField(this.remind,
                SendRemind.nextDate(this.remind.getRemindDate().split("")));
        RemindServiceImpl.newRemindService().updateCountSendField(this.remind,0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(this.remind, true);
        count = 0;}
    }


    public String dateToSend(){
    return RemindServiceImpl.newRemindService().getRemindById(undeletedIndex).getRemindDate();}
    }


