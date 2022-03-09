package telegramBot.hidenPackage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.hidenPackage.entity.RemindDPer;
import telegramBot.hidenPackage.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

public class SendAnotherRemind {
    private final SendMessageService service;
    @Getter
    private static boolean isStop = false;

    @Autowired
    public SendAnotherRemind(SendMessageServiceImpl sendMessageService){
        this.service = sendMessageService;
    }

    public void send(String stopCommand) {
        RemindDPer remindDPer = RemindServiceImpl.newRemindService().getRemindById(1);
        int countSendRemind = 0;
        while (isConditionsToSend(stopCommand)) {
            if (telegramBot.sendRemind.
                    SendRemind.timeDifference(remindDPer.getLastSendTime()) >= 0.05) {
                if (this.service.sendMessage(remindDPer.getChatId(), remindDPer.
                        getRemindAboutTablets())) {
                    RemindServiceImpl.newRemindService().
                            updateCountSendField(remindDPer, countSendRemind + 1);

                    RemindServiceImpl.newRemindService().updateLastSendTimeField(remindDPer,
                            telegramBot.sendRemind.SendRemind.currentTime());
                }
            }
        }
        if((countSendRemind = remindDPer.getCount_send()) > 0){
        String nextDate = telegramBot.sendRemind.SendRemind.
                nextDate(remindDPer.getRemindDate().split(""));

        RemindServiceImpl.newRemindService().updateRemindDateField(remindDPer, nextDate);
        RemindServiceImpl.newRemindService().updateLastSendTimeField(remindDPer,"18:00");
        RemindServiceImpl.newRemindService().updateCountSendField(remindDPer, 0);
        SendAnotherRemind.isStop = false;

        }

    }

    public boolean isConditionsToSend(String command){
        double time = Double.parseDouble(telegramBot.sendRemind.SendRemind.
                        currentTime().replace(':', '.'));
        boolean result = (time >= 18.00 && time <= 20.10 && !command.equals("/done"));
        if(result) isStop = false;
        isStop = true;
        return result;

    }

}
