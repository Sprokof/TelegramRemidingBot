package telegramBot.manage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Remind;
import telegramBot.service.*;

import java.util.*;


@Component
public class RemindManage {

    @Autowired
    private RemindServiceImpl remindService;

    private final DateManage dateManage;

    @Autowired
    private UserService userService;

    @Getter
    private static final String REMIND_MESSAGE = "(R) Позвольте напомнить, что вам следует ";
    private static final String SHOW_MESSAGE = "(R) На эту дату есть следующие записи:\n";
    private static final int COUNT_SEND_REMIND = 4;
    private static final double HOUR = 3.01;

    @Autowired
    public RemindManage() {
        this.dateManage = new DateManage();

    }


    public synchronized void execute() throws InterruptedException {
        List<Integer> remindsId;
        while ((remindsId = remindService.getIdOfAllReminds()).isEmpty()) {
            wait();
        }
        List<Remind> reminds = new ArrayList<>();
        for (Integer integer : remindsId) {
            Remind remind = remindService.getRemindById(integer);
            if ((remind.getRemindDate().equals(DateManage.currentDate())
                    && (remind.getUser().isActive()))) {
                changeRemind(reminds, remind);
            }
        }

        reminds.forEach((remind) -> {
            List<Remind> waitsExecuteReminds = null;
            while (!(waitsExecuteReminds =
                    remindService.getAllExecutingReminds(remind)).isEmpty()) {
                while (true) {
                    if (send(waitsExecuteReminds)) {
                        break;
                    }
                }
            }
        });
        deleteNotUpdatedRemind();
    }


    private boolean isContainsDailySendMarker(String maintenance) {
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }


    private static String deleteRegularMarker(Remind remind) {
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getDetails().getKey());
        char fLetter = Character.toLowerCase(decrypt.charAt(decrypt.indexOf(" ") + 1));
        return String.format("%s%s", fLetter, decrypt.substring(decrypt.indexOf(" ") + 2));
    }

    public synchronized boolean showRemindsByDate(String userChatId, String date) throws InterruptedException {
        List<Integer> remindsId;
        while ((remindsId = remindService.getIdOfAllReminds()).size() == 0) {
            wait();
        }

        int index = 0, count = 0, number = 1;
        this.remindService.sendRemind(userChatId, "Через пару секунд пришлю напоминания на "
                + DateManage.dayAndMonth(date));
        Thread.sleep(4700);
        String messageToSend = SHOW_MESSAGE;

        while (index != remindsId.size()) {
            Remind remind = remindService.getRemindById(remindsId.get(index));
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getDetails().getKey());
            if ((remind.getUser().getChatId().equals(userChatId) &&
                    remind.getRemindDate().equals(date.replaceAll("\\p{P}", "\\.")))
                    && !isContainsDailySendMarker(decrypt)) {
                messageToSend = messageToSend + (number ++) + ") " + decrypt + "\n";
                count++;
            }
            index++;
        }
        if (count != 0) this.remindService.sendRemind(userChatId, messageToSend);
        Thread.sleep(5700);
        return count > 0;
    }

    private String messageForOneRemind(Remind remind) {
        String messageToSend = REMIND_MESSAGE;
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getDetails().getKey());

        if (isContainsDailySendMarker(decrypt)) {
            messageToSend = messageToSend + deleteRegularMarker(remind);
        } else {
            messageToSend = (messageToSend +
                    String.valueOf(decrypt.
                            charAt(0)).toLowerCase(Locale.ROOT) + decrypt.substring(1));
        }

        updateRemindFieldsToNextSendTime(remind, remind.getDetails().getCountSendOfRemind() + 1);
        if (remind.getDetails().getCountSendOfRemind() == COUNT_SEND_REMIND) {
            if (isContainsDailySendMarker(decrypt)) {
                String date = DateManage.nextDate(remind.getRemindDate());
                updateRemindFieldsToNextDay(remind, date);
            } else {
                userService.deleteUserRemind(remind);
            }

        }
        return String.format("%s%s", messageToSend+"\n", "/instr - сводка по командам.");
    }


    private String messageForAggregateRemind(Remind[] reminds) {
        String messageToSend = REMIND_MESSAGE + "сделать следующее:\n";
        String maintenance;
        for (int i = 0; i < reminds.length; i++) {
            Remind remind = reminds[i];
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getDetails().getKey());
            int num = (i + 1);
            if (isContainsDailySendMarker(decrypt)) {
                maintenance = String.format("%s%s", Character.
                        toUpperCase(deleteRegularMarker(remind).charAt(0)), deleteRegularMarker(remind).substring(1));
            } else {
                maintenance = String.valueOf(decrypt.
                        charAt(0)).toUpperCase(Locale.ROOT) + decrypt.substring(1);
            }

            messageToSend = messageToSend + num + ") " + maintenance + "." + "\n";

            updateRemindFieldsToNextSendTime(reminds[i],
                    reminds[i].getDetails().getCountSendOfRemind() + 1);
        }


        for (Remind remind : reminds) {
            String decrypted = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getDetails().getKey());

            if ((isContainsDailySendMarker(decrypted))) {
                if (remind.getDetails().getCountSendOfRemind() == COUNT_SEND_REMIND) {
                    String date = DateManage.nextDate(remind.getRemindDate());
                    updateRemindFieldsToNextDay(remind, date);
                }
            } else {
                if (remind.getDetails().getCountSendOfRemind() == COUNT_SEND_REMIND) {
                    userService.deleteUserRemind(remind);
                }
            }
        }

        return String.format("%s%s", messageToSend+"\n", "/instr - сводка по командам.");
    }


    private void changeRemind(List<Remind> reminds, Remind remind) {
        double time = TimeManage.toDoubleTime(TimeManage.currentTime());
        if (!remind.getDetails().isTimeToSend()) {
            if ((TimeManage.timeDifference(remind.getDetails().getLastSendTime()) >= HOUR) && (time < 23.05)) {
                remindService.updateTimeToSendField(remind, true);
                reminds.add(remindService.getRemindById(remind.getId()));
                remindService.updateSendHourField(remind, TimeManage.currentTime());

            }
        }
        if (time >= 23.05 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)
                || time <= 3 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)) {

            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getDetails().getKey());

            if (isContainsDailySendMarker(decrypt)) {
                String date = DateManage.nextDate(remind.getRemindDate());
                updateRemindFieldsToNextDay(remind, date);
            } else {
                userService.deleteUserRemind(remind);

            }
        }
    }

    public void updateRemindFieldsToNextDay(Remind remind, String date) {
        remindService.updateRemindDateField(remind, date).
                updateCountSendField(remind, 0).
                updateTimeToSendField(remind, false).
                updateSendHourField(remind, TimeManage.DEFAULT_TIME);
    }

    public void updateRemindFieldsToNextSendTime(Remind remind, int count) {
        remindService.updateCountSendField(remind, count).
                updateTimeToSendField(remind, false).
                updateSendHourField(remind, TimeManage.currentTime());
    }

    private void deleteNotUpdatedRemind() {
        List<Remind> reminds = remindService.getAllRemindsFromDB();
        reminds.forEach((r) -> {
            if (dateManage.isRemindDateBefore(r.getRemindDate())) {
                userService.deleteUserRemind(r);
            }
        });
    }
    

    private boolean send(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        Remind remind = reminds.get(0);
        String maintenance, chatId = String.valueOf(remind.getUser().getChatId());

        if (reminds.size() == 1) {
            maintenance = messageForOneRemind(remind);
        } else {
            maintenance = messageForAggregateRemind(reminds.toArray(Remind[]::new));
        }

        if(this.remindService.sendRemind(chatId, maintenance)) {
            this.remindService.markAsExecuted(remind);
        }
        return true;
    }

}


























