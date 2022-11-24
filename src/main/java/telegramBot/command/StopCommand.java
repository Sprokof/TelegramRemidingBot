package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.User;
import telegramBot.service.*;

public class StopCommand implements Command {
    public static String[] STOP_COMMANDS = {"Вы остановили напоминания. /restart - для возообновления " +
            "(Остановленные на сутки и более ежедневные напоминания - удаляются).",
            "Невозможно остановить неактивные напоминания."};

    private final SendMessageService sendMessageService;
    private final UserService userService;

    public StopCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.userService = new UserServiceImpl(new UserDAOImpl());
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(!stop(chatId)){
        return this.sendMessageService.sendMessage(chatId, STOP_COMMANDS[1]);}
        else return this.sendMessageService.sendMessage(chatId, STOP_COMMANDS[0]);
    }

    private boolean stop(String chatId) {
        User user = this.userService.getUserByChatId(chatId);
        if(user.isActive()) {
            user.setActive(false);
            this.userService.updateUser(user);
            return true;
        }
        else return false;
    }
}




