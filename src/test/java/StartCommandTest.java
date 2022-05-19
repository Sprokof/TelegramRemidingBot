import org.mockito.Mockito;
import telegramBot.command.Command;
import telegramBot.command.StartCommand;
import telegramBot.service.SendMessageService;

import static telegramBot.command.StartCommand.START_COMMANDS;
import static telegramBot.command.CommandName.START;

public class StartCommandTest extends AbstractCommandTest {
    @Override
    String getCommandName() {
        return START.getCommandName();
    }

    @Override
    Command getCommand() {
        SendMessageService sendMessageService = Mockito.mock(SendMessageService.class);
        return new StartCommand(sendMessageService);
    }


    @Override
    String getCommandMessage() {
        return START_COMMANDS[0];
    }
}

