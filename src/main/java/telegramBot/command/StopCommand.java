package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;

public class StopCommand implements Command {
    public static String STOP_COMMAND = "Вы остановили напоминания. /restart - для возообновления";
    private final SendMessageService sendMessageService;


    public StopCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(!stop(chatId))
            STOP_COMMAND = "Невозможно остановить неактивные напоминания";
        sendMessageService.sendMessage(chatId, STOP_COMMAND);
        return true;
    }

    private boolean stop(String chatId) {
        int count = 0;
        for (Remind r : RemindServiceImpl.newRemindService().getAllRemindsFromDB()) {
            Details details = r.getDetails();
            if (details.getChatIdToSend().equals(chatId) && details.getIsStop().equals("false")) {
                RemindServiceImpl.newRemindService().updateIsStopField(r, true);
                count++;
            }
        }
        return count > 0;
    }
}




