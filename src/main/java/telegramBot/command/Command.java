package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    boolean execute(Update update);
}
