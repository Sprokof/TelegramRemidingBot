package telegramBot.command;

import com.google.common.collect.ImmutableMap;
import telegramBot.service.SendMessageService;

import static telegramBot.command.CommandName.*;

public class CommandContainer {
    private ImmutableMap<String, Command> commandContainer;
    private UnknownCommand unknownCommand;
    public CommandContainer(SendMessageService sendMessage) {
        this.commandContainer = ImmutableMap.<String, Command>builder().
                put(STOP.getCommandName(), new StopCommand(sendMessage)).
                put(UNKNOWN.getCommandName(),new UnknownCommand(sendMessage)).
                put(START.getCommandName(), new StartCommand(sendMessage)).
                put(ADD.getCommandName(), new AddCommand(sendMessage)).
                put(RESTART.getCommandName(), new RestartCommand(sendMessage)).
                build();
        this.unknownCommand = new UnknownCommand(sendMessage);

    }

    public Command retrieveCommand(String command){
        return this.commandContainer.getOrDefault(command, unknownCommand);}
    }

