package telegramBot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import telegramBot.command.CommandName;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.*;
import telegramBot.manage.*;
import telegramBot.service.DeleteMessageServiceImpl;
import telegramBot.command.CommandContainer;

import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageServiceImpl;
import telegramBot.service.UserService;

import static telegramBot.service.UserServiceImpl.*;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final static Map<String, List<String>> commands;
    static Logger log = Logger.getLogger("Logger");

    static {
        commands = new HashMap<>();
    }


    @Autowired
    private UserService userService;
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    private final String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;


    @Getter
    @Autowired
    private RemindServiceImpl remindService;

    private final SendMessageServiceImpl sendMessageService;
    @Autowired
    private RemindManage manage;
    @Autowired
    private DeleteMessageServiceImpl deleteMessageService;


    private static final String[] messagesToLog = {"METHOD STARTS", "METHOD FINISHED"};

    public TelegramBot() {
        this.sendMessageService = new SendMessageServiceImpl(this);
        this.commandContainer = new CommandContainer(sendMessageService);

    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = "";
        String chatId;
        User user;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId().toString();
            user = userService.createUser(chatId);
            commands.putIfAbsent(chatId, new ArrayList<String>());
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                command = message.split(" ")[0].toLowerCase(Locale.ROOT);
                this.commandContainer.retrieveCommand(command).execute(update);
                commands.get(chatId).add(command);
            } else {
                if (lastCommand(chatId).equals(CommandName.ADD.getCommandName())) {
                    acceptNewRemindFromUser(user, update);
                } else if (lastCommand(chatId).equals(CommandName.SHOW.getCommandName())) {
                    if (acceptDateFromUser(update)) {
                        try {
                            if (!this.manage.showRemindsByDate(chatId, message)) {
                                this.deleteMessageService.deleteMessage(chatId,
                                        SendMessageServiceImpl.getMessageId() - 1);
                                this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                                        "Не получилось найти напоминания, возможно " +
                                                "вы указали уже прошедшую дату, либо на эту дату нет напоминаний");
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.deleteMessageService.deleteMessage(chatId,
                                SendMessageServiceImpl.getMessageId() - 1);
                        this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                                "Вероятно вы ошиблись в формате даты. Повторите команду /show и " +
                                        "введите дату в верном формате");
                    }
                } else if (unknownInput(chatId)) {
                    this.commandContainer.retrieveCommand(CommandName.
                            UNKNOWN.getCommandName()).execute(update);
                }
            }


        }
        executeReminds();

    }

    private String getDateFromUserInput(String input) {
        int firstIndexOfDate = 0;
        int lastIndexOfDate = input.length();
        for (int i = 0; i < input.split("").length; i++) {
            if (String.valueOf(input.charAt(i)).matches("[0-9]")
                    && String.valueOf(input.charAt(i + 1)).
                    matches("\\p{P}")) {
                firstIndexOfDate = (i - 1);
                break;
            }
        }
        return input.substring(firstIndexOfDate, lastIndexOfDate);
    }


    private String getRemindContentFromUserInput(String input) {
        return input.substring(0, input.length() - getDateFromUserInput(input).length());
    }


    private boolean isCorrectInput(String input) {
        Pattern p = Pattern.compile("[Aa-zZ\\s][0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean textWithRightDate = p.matcher(input).find();
        if (textWithRightDate) {
            return DateManage.validateDate(getDateFromUserInput(input).split("\\p{P}")[0],
                    getDateFromUserInput(input).split("\\p{P}")[1],
                    getDateFromUserInput(input).split("\\p{P}")[2]);
        } else {
            return false;
        }
    }


    private void executeReminds() {
        final long[] milliseconds = new long[2];
        final long mills = 870000;
        new Thread(() -> {
            try {
                while (true) {
                    consoleLog(messagesToLog[0], milliseconds, 0);
                    TelegramBot.this.manage.execute();
                    consoleLog(messagesToLog[1], milliseconds, 1);
                    Thread.sleep(mills);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public String lastCommand(String chatId) {
        int size = commands.get(chatId).size();
        if(size == 0) return "";
        int lastIndex = size - 1;
        return commands.get(chatId).get(lastIndex);

    }


    private void consoleLog(String message, long[] aMills, int index) {
        aMills[index] = getMills();
        if (index == 1) {
            message = message + " FOR " + (aMills[1] - aMills[0]) + " MILLISECONDS";
        }
        log.log(Level.SEVERE, message);
    }


    private boolean acceptDateFromUser(Update update) {
        String input = update.getMessage().getText();
        Pattern p = Pattern.compile("[0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean isDate = p.matcher(input).find();
        boolean result = false;
        if (isDate) {
            String[] dateArray = input.split("\\p{P}");
            result = DateManage.validateDate(dateArray[0], dateArray[1], dateArray[2]);
        }

        return result;

    }

    private synchronized void acceptNewRemindFromUser(User user, Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String input = update.getMessage().getText();
        Integer messageId = update.getMessage().getMessageId();
        boolean isExist;
        if (isCorrectInput(input)) {
            try {
                String key = XORCrypt.keyGenerate();
                String encrypt = XORCrypt.encrypt(getRemindContentFromUserInput(input), key).
                        replaceAll("\u0000", "");

                Remind remind = new Remind(encrypt,
                        getDateFromUserInput(input).
                                replaceAll("\\p{P}", "\\."));

                remind.setDetails(new Details(key, false, 0));

                isExist = remindService.isExistRemind(user, remind);
                if (!isExist) {
                    remindService.extendsLastSendTimeIfAbsent(remind);
                    addUserRemind(this.remindService, remind);
                    notify();
                    Thread.sleep(370);

                    this.deleteMessageService.deleteMessage(chatId, messageId);
                    this.sendMessageService.sendMessage(chatId, "Напоминание успешно" +
                            " добавлено.");
                } else {
                    this.deleteMessageService.deleteMessage(chatId, messageId);
                    this.sendMessageService.sendMessage(chatId,
                            "Данное напоминание было добавлено ранее.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.deleteMessageService.deleteMessage(chatId, messageId);
            this.sendMessageService.sendMessage(chatId,
                    "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy) . " +
                            "Возможно, что вы указали уже прошедшую дату. " +
                            "После введите команду /add для повторного добавления.");
        }

        commands.get(chatId).clear();

    }

    private long getMills() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public boolean unknownInput(String chatId) {
        if (commands.get(chatId).isEmpty()) return true;
        return !(lastCommand(chatId).equals("/add") && lastCommand(chatId).equals("/show"));
    }
}


























