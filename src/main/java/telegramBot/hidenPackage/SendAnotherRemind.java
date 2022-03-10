package telegramBot.hidenPackage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.bot.TelegramBot;
import telegramBot.hidenPackage.entity.RemindDPer;
import telegramBot.hidenPackage.service.RemindServiceImpl;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendAnotherRemind  {
    private final SendMessageService service;

    @Getter
    private static boolean isDoneOnToday = false;

    @Autowired
    public SendAnotherRemind(SendMessageServiceImpl sendMessageService){
        this.service = sendMessageService;
    }

    private void send(String stopCommand) {
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

    private boolean isConditionsToSend(String command){
        double time = SendRemind.toDoubleTime();
        return (time >= 18.00 && time <= 20.10 && !command.equals("/done"));

    }

    public boolean specialConditions(String chatId, String command, Map<String,
            List<String>> commands, SendMessageServiceImpl sendMessageService) {
        RemindDPer remindDPer = telegramBot.hidenPackage.
                service.RemindServiceImpl.newRemindService().getRemindById(1);
        if(!chatId.equals(remindDPer.getChatId())) return false;

        if(remindDPer.getCount_send() >= 1) {
            if (command.equals("/done")) {
                commands.get(chatId).add(command);
                sendMessageService.sendMessage(chatId, "SPAM IS STOP");
                commands.get(chatId).clear();
                return true;

            } else sendMessageService.sendMessage(chatId, "wrong command to stop. " +
                    "You need /done");
            return false;
        }
        else {
            sendMessageService.sendMessage(chatId, "Еще не было выслано напоминаний"); }
        return false;
    }


    public void execute(Map<String, List<String>> commands, TelegramBot bot) {
        String chatId = telegramBot.hidenPackage.service.RemindServiceImpl.
                newRemindService().getRemindById(1).getChatId();

        commands.putIfAbsent(chatId, new ArrayList<>());
        String stop;
        String executeDate = telegramBot.hidenPackage.service.
                RemindServiceImpl.newRemindService().getRemindById(1).getRemindDate();
        if (!bot.lastCommand(chatId).equals("/done")) {
            stop = "";
        } else stop = bot.lastCommand(chatId);
        if(executeDate.equals(SendRemind.currentDate())){ send(stop);}
    }

    public static void changeFlag(){
        if(!isDoneOnToday){
            if(SendRemind.toDoubleTime() >= 20.15)
            isDoneOnToday = true;
        }
    }
}



