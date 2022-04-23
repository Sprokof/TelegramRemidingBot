package telegramBot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import telegramBot.crypt.XORCrypt;
import telegramBot.entity.*;
import telegramBot.manage.*;
import telegramBot.service.DeleteMessageServiceImpl;
import telegramBot.service.MessageServiceImpl;
import telegramBot.command.CommandContainer;
import telegramBot.service.SendMessageServiceImpl;

import static telegramBot.service.UserServiceImpl.*;
import static telegramBot.service.RemindServiceImpl.*;


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

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    private final String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;
    @Getter
    private final SendMessageServiceImpl sendMessageService;
    private final RemindManage manage;
    private final DeleteMessageServiceImpl deleteMessageService;
    private static final String[] messagesToLog = {"METHOD STARTS", "METHOD FINISHED"};

    public TelegramBot() {
        this.sendMessageService = new SendMessageServiceImpl(this);
        this.commandContainer = new CommandContainer(sendMessageService);
        this.deleteMessageService = new DeleteMessageServiceImpl(this);
        this.manage = new RemindManage(sendMessageService, deleteMessageService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = "";
        String chatId;
        User user;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId().toString();
            user = createUser(chatId);
            commands.putIfAbsent(chatId, new ArrayList<String>());
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                command = message.split(" ")[0].toLowerCase(Locale.ROOT);
                this.commandContainer.retrieveCommand(command).execute(update);
                commands.get(chatId).add(command); saveCommand(user); saveCommandMessage(user);
            } else {
                if (lastCommand(chatId).equals("/add")) {
                    acceptNewRemindFromUser(update);
                } else if (lastCommand(chatId).equals("/show")) {
                    if (acceptDateFromUser(update)) {
                        try {
                            if (!this.manage.showRemindsByDate(update.getMessage().getChatId().toString(),
                                    update.getMessage().getText())) {
                                this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                                        "Не получилось найти напоминания, возможно " +
                                                "вы указали уже прошедшую дату, либо на эту дату нет напоминаний");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                                "Вероятно вы ошиблись в формате даты. Повторите команду /show и " +
                                        "введите дату в верном формате");
                    }
                    saveCommandMessage(user);
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

    public synchronized String lastCommand(String chatId) {
        while (commands.isEmpty() || commands.get(chatId).isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException | NullPointerException e) {
                e.getCause();
            }
        }

        int lastIndex = commands.get(chatId).size() - 1;
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
        if (isDate) {
            String[] dateArray = input.split("\\p{P}");
            return DateManage.validateDate(dateArray[0], dateArray[1], dateArray[2]);
        } else {
            return false;
        }

    }

    private synchronized void acceptNewRemindFromUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String input = update.getMessage().getText();
        Integer messageId = update.getMessage().getMessageId();
        User user = userService().getUserByChatId(chatId);
        boolean isExist;
        if (isCorrectInput(input)) {
            try {
                String key = XORCrypt.keyGenerate();
                String encrypt = XORCrypt.encrypt(getRemindContentFromUserInput(input), key).
                        replaceAll("\u0000", "");

                Remind remind = new Remind(encrypt,
                        getDateFromUserInput(input).
                                replaceAll("\\p{P}", "\\."));

                double temp = TimeManage.toDoubleTime(TimeManage.currentTime()) - 2;
                String time;
                if ((time = TimeManage.
                        toStringTime(temp)).length() == 1) {
                    time = time + "0";
                }

                Details details = new Details(key, time, false, 0);

                isExist = remindService().isExistRemind(user, remind, details);
                if (!isExist) {
                    String maxTime;
                    if ((maxTime = remindService().getMaxTime(remind)) != null) {
                        remind.getDetails().setLastSendTime(maxTime);
                    }
                    addUserRemind(remind);
                    notify();
                    this.deleteMessageService.deleteMessage(new Message(chatId,
                            messageId));
                    Thread.sleep(370);
                    this.sendMessageService.sendMessage(chatId, "Напоминание успешно" +
                            " добавлено.");
                } else {
                    this.deleteMessageService.deleteMessage(new Message(chatId,
                            messageId));
                    this.sendMessageService.sendMessage(chatId,
                            "Данное напоминание было добавлено ранее.");
                }
                saveCommandMessage(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.deleteMessageService.deleteMessage(new Message(chatId, SendMessageServiceImpl.
                    getMessageId()));
            this.sendMessageService.sendMessage(chatId,
                    "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy) . " +
                            "Возможно, что вы указали уже прошедшую дату. " +
                            "После введите команду /add для повторного добавления.");

            commands.get(chatId).clear();
        }
        saveCommand(user);
        saveCommandMessage(user);

    }

    private long getMills() {
        return Calendar.getInstance().getTimeInMillis();
    }


    private User createUser(String chatId) {
        User user;
        if ((user = userService().getUserByChatId(chatId)) == null) {
            userService().saveUser(new User(chatId, true));
        }
        return user;
    }

    private void saveCommand(User user) {
        Message command = new Message(user.getChatId(), "0",
                (SendMessageServiceImpl.getMessageId() - 1), false);
        MessageServiceImpl.messageService().save(command);
    }

    private void saveCommandMessage(User user) {
        Message command = new Message(user.getChatId(), "0",
                (SendMessageServiceImpl.getMessageId()), false);
        MessageServiceImpl.messageService().save(command);
    }
}

























