package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.User;

import telegramBot.service.SendMessageService;
import telegramBot.service.UserService;
import telegramBot.service.UserServiceImpl;


public class RestartCommand implements Command {
    public static String[] RESTART_COMMANDS = {"Вы возообновили напоминания.",
            "Невозможно возообновить активные напоминания."};

    private SendMessageService sendMessageService;
    private UserService userService;

    public RestartCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.userService = new UserServiceImpl(new UserDAOImpl());
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if (!restart(chatId)){
        return this.sendMessageService.sendMessage(chatId, RESTART_COMMANDS[1]);
        }
        else {
            return this.sendMessageService.sendMessage(chatId, RESTART_COMMANDS[0]);
        }
    }

    private boolean restart(String chatId) {
       User user = this.userService.getUserByChatId(chatId);
       if(!user.isActive()) {
           user.setActive(true);
           this.userService.updateUser(user);
           return true; }
       else return false;

    }
}

