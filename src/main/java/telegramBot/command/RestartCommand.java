package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.entity.User;
import static telegramBot.service.UserServiceImpl.*;
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
       User user = newUserService().getUserByChatId(chatId);
       if(!user.isActive()) {
           user.setActive(true);
           newUserService().updateUser(user);
           return true; }
       else return false;

    }
}

