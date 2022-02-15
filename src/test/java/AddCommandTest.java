import org.junit.Test;
import org.mockito.Mockito;
import telegramBot.command.AddCommand;
import telegramBot.command.Command;
import telegramBot.service.SendMessageService;

import static telegramBot.command.CommandName.ADD;
import static telegramBot.command.AddCommand.ADD_COMMAND;



public class AddCommandTest extends AbstractCommandTest{

    @Override
    String getCommandName() {
        return ADD.getCommandName();
    }

    @Override
    Command getCommand() {
    SendMessageService sendMessageService = Mockito.mock(SendMessageService.class);
            return new AddCommand(sendMessageService);
        }


    @Override
    String getCommandMessage() {
        return ADD_COMMAND;
    }
}
