import org.mockito.Mockito;
import telegramBot.command.Command;
import telegramBot.service.SendMessageService;

import static telegramBot.command.CommandName.STOP;
import static telegramBot.command.CommandName.UNKNOWN;
import static telegramBot.command.StopCommand.STOP_COMMAND;

public class UnknownCommandTest extends AbstractCommandTest {
    @Override
    String getCommandName() {
        return UNKNOWN.getCommandName();
    }

    @Override
    Command getCommand() {
        SendMessageService sendMessageService = Mockito.mock(SendMessageService.class);
        return new telegramBot.command.UnknownCommand(sendMessageService);
    }

    @Override
    String getCommandMessage() {
        return UNKNOWN.getCommandName();
    }


}

