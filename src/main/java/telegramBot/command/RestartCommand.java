package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.entity.Message;
import telegramBot.entity.User;

import static telegramBot.service.MessageServiceImpl.messageService;
import static telegramBot.service.UserServiceImpl.*;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

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
        Message output = new Message(chatId, "0",
                SendMessageServiceImpl.getMessageId(), false);
            messageService().save(output);
        return true;
    }

    private boolean restart(String chatId) {
       User user = userService().getUserByChatId(chatId);
       if(!user.isActive()) {
           user.setActive(true);
           userService().updateUser(user);
           return true; }
       else return false;

    }
}

