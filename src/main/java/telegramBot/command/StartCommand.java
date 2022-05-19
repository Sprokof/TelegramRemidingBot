package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Message;
import telegramBot.entity.User;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;
import telegramBot.service.UserServiceImpl;

import static telegramBot.service.MessageServiceImpl.messageService;

public class StartCommand implements Command{

    public static final String[] START_COMMANDS = {
            "Я бот, реализующий напоминательную функцию. Для получения информации " +
                    "об моей работе введите команду /instr", "Комманда уже была запущена ранее." +
            "\n/instr - сводка по коммандам."
    };

    private final SendMessageService sendMessageService;

    public StartCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}


    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        User user = UserServiceImpl.userService().getUserByChatId(chatId);
        if(!user.isStarted()) {
            user.setStarted(true);
            UserServiceImpl.userService().updateUser(user);
            return this.sendMessageService.sendMessage(chatId, START_COMMANDS[0]);
        }
        return this.sendMessageService.sendMessage(chatId, START_COMMANDS[1]);
    }
}

