package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Details;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import static telegramBot.service.MessageServiceImpl.messageService;
import static telegramBot.service.UserServiceImpl.userService;

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
        return this.sendMessageService.sendMessage(chatId, STOP_COMMANDS[1]);}
        else return this.sendMessageService.sendMessage(chatId, STOP_COMMANDS[0]);
    }

    private boolean stop(String chatId) {
        User user = userService().getUserByChatId(chatId);
        if(user.isActive()) {
            user.setActive(false);
            userService().updateUser(user);
            return true; }
        else return false;
    }
}




