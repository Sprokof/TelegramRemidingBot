package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;

public class StopCommand implements Command {
    public static String[] STOP_COMMANDS = {"Вы остановили напоминания. /restart - для возообновления " +
            "(Остановленные на сутки и более ежедневные напоминания - удаляются).",
            "Невозможно остановить неактивные напоминания."};
    private final SendMessageService sendMessageService;


    public StopCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(!stop(chatId)){
        sendMessageService.sendMessage(chatId, STOP_COMMANDS[1]);}
        else sendMessageService.sendMessage(chatId, STOP_COMMANDS[0]);
        return true;
    }

    private boolean stop(String chatId) {
        int count = 0;
        for (Remind r : RemindServiceImpl.newRemindService().getAllRemindsFromDB()) {
            Details details = r.getDetails();
            if ((details.getChatIdToSend() == Integer.parseInt(chatId)) && (!details.isStop())) {
                RemindServiceImpl.newRemindService().updateIsStopField(r, true);
                count++;
            }
        }
        System.out.println(count+ "remind");
        return count > 0;
    }
}




