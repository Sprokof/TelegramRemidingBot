package telegramBot.hidenPackage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.hidenPackage.entity.RemindDPer;
import telegramBot.hidenPackage.service.RemindServiceImpl;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

public class SendAnotherRemind  {
    private final SendMessageService service;

    @Getter
    private static boolean isDoneOnToday = false;

    @Autowired
    public SendAnotherRemind(SendMessageServiceImpl sendMessageService){
        this.service = sendMessageService;
    }

    public void send(String stopCommand) {
        RemindDPer remindDPer = RemindServiceImpl.newRemindService().getRemindById(1);
        if (isConditionsToSend(stopCommand)) {
            isDoneOnToday = false;
            if (telegramBot.sendRemind.
                    SendRemind.timeDifference(remindDPer.getLastSendTime()) >= 0.044) {
                if (this.service.sendMessage(remindDPer.getChatId(), remindDPer.
                        getRemindAboutTablets())) {
                    RemindServiceImpl.newRemindService().
                            updateCountSendField(remindDPer, remindDPer.getCount_send() + 1);

                    RemindServiceImpl.newRemindService().updateLastSendTimeField(remindDPer,
                            telegramBot.sendRemind.SendRemind.currentTime());
                }
            }
        }
        else{
            if((remindDPer.getCount_send()) > 0) {
                String nextDate = telegramBot.sendRemind.SendRemind.
                        nextDate(remindDPer.getRemindDate().split(""));

                RemindServiceImpl.newRemindService().updateRemindDateField(remindDPer, nextDate);
                RemindServiceImpl.newRemindService().updateLastSendTimeField(remindDPer, "17:55");
                RemindServiceImpl.newRemindService().updateCountSendField(remindDPer, 0);
                isDoneOnToday = true;
            }

        }

    }

    public boolean isConditionsToSend(String command){
        double time = SendRemind.toDoubleTime();
        return (time >= 18.00 && time <= 20.10 && !command.equals("/done"));

    }

}
