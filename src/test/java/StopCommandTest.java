import org.mockito.Mockito;
import telegramBot.command.AddCommand;
import telegramBot.command.Command;
import telegramBot.service.SendMessageService;

import static telegramBot.command.StopCommand.STOP_COMMAND;
import static telegramBot.command.CommandName.STOP;

public class StopCommandTest extends AbstractCommandTest{

    @Override
    String getCommandName() {
        return STOP.getCommandName();
    }

    @Override
    Command getCommand() {
        SendMessageService sendMessageService = Mockito.mock(SendMessageService.class);
        return new telegramBot.command.StopCommand(sendMessageService);
    }

    @Override
    String getCommandMessage() {
        return STOP_COMMAND;
    }


}
