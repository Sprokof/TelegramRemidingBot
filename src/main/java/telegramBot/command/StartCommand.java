package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.User;
import telegramBot.service.SendMessageService;
import telegramBot.service.UserService;
import telegramBot.service.UserServiceImpl;


public class StartCommand implements Command{

    public static final String[] START_COMMANDS = {
            "Я бот, реализующий напоминательную функцию. Для получения информации " +
                    "об моей работе введите команду /instr", "Комманда уже была запущена ранее." +
            "\n/instr - сводка по коммандам."
    };

    private final SendMessageService sendMessageService;
    private final UserService userService;


    public StartCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;
        this.userService = new UserServiceImpl(new UserDAOImpl());
    }


    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        User user = this.userService.getUserByChatId(chatId);
        if(!user.isStarted()) {
            user.setStarted(true);
            userService.updateUser(user);
            return this.sendMessageService.sendMessage(chatId, START_COMMANDS[0]);
        }
        return this.sendMessageService.sendMessage(chatId, START_COMMANDS[1]);
    }
}

