package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Remind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;

public class RestartCommand implements Command {
    public static String[] RESTART_COMMANDS = {"Вы возообновили напоминания.",
            "Невозможно возообновить активные напоминания."};

    private SendMessageService sendMessageService;

    public RestartCommand(SendMessageService sendMessageService) {

        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if (!restart(chatId)){
        this.sendMessageService.sendMessage(chatId, RESTART_COMMANDS[1]);}
        else this.sendMessageService.sendMessage(chatId, RESTART_COMMANDS[0]);
        return true;
    }

    private boolean restart(String chatId) {
        int count = 0;
        for (Remind r : RemindServiceImpl.newRemindService().getAllRemindsFromDB()) {
            if (r.getDetails().getChatIdToSend() == Integer.parseInt(chatId)
            && r.getDetails().isStop()) {
                RemindServiceImpl.newRemindService().updateIsStopField(r, false);
                count++;
            }
        }
        return count > 0;

    }
}

