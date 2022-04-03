package telegramBot.manage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.service.*;

import java.util.*;

@Component
public class RemindManage {


    private SendMessageServiceImpl service;
    private DeleteMessageServiceImpl deleteService;
    @Getter
    private static final String REMIND_MESSAGE = "(R) Позвольте напомнить, что вам следует ";
    private static final String SHOW_MESSAGE = "(R) На эту дату есть следующие записи:\n";

    @Autowired
    public RemindManage(SendMessageService service, DeleteMessageServiceImpl deleteService) {
        this.service = (SendMessageServiceImpl) service;
        this.deleteService = deleteService;
    }

    private synchronized int[] getIdOfAllReminds() throws InterruptedException {
        List<Remind> reminds;
        while ((reminds = RemindServiceImpl.newRemindService().
                getAllRemindsFromDB()).isEmpty()) {
            wait();
        }
        notify();
        int[] ides = null;
        try {
            ides = new int[reminds.size()];
            Remind remind;
            for (int i = 0; i < ides.length; i++) {
                remind = reminds.get(i);
                ides[i] = remind.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ides;
    }

    public void execute() throws InterruptedException {
        int[] remindId = getIdOfAllReminds();
        List<Remind> reminds = new ArrayList<>();
        for (int index = 0; index < remindId.length; index++) {
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindId[index]);
            if (remind.getRemindDate().equals(DateManage.currentDate())) {
                if(isChangedRemind(remind, remindId[index]) || isUpdatedToNextDay(remind)) {
                    reminds.add(remind);}
            }
        }

        reminds.forEach((r) -> {
            List<Remind> waitsExecuteReminds = null;
               while(!(waitsExecuteReminds =
                       RemindServiceImpl.newRemindService().getAllExecutingReminds(r)).isEmpty()) {
                   while (true) {
                       if (send(waitsExecuteReminds)) {
                           break;
                       }
                   }
               }
            });

        if(TimeManage.toDoubleTime(TimeManage.currentTime()) >= 23.10) {
            List<Message> messages;
            if (!(messages = MessageServiceImpl.newMessageService().getAllMessages()).isEmpty()) {
                messages.forEach((m) -> {
                    this.deleteService.deleteMessage(m);

                });
            MessageServiceImpl.newMessageService().deleteAllMessages();
                System.out.println(messages.size());}
        }
        deleteNotUpdatedRemind();
    }



    private boolean isContainsDailySendMarker(String maintenance) {
        return (maintenance.split("")[0].
                equalsIgnoreCase("Р") && maintenance.split("")[1].equals(" "));
    }


    private static String deleteRegularMarker(Remind remind) {
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getKey());
        char fLetter = Character.toLowerCase(decrypt.charAt(decrypt.indexOf(" ") + 1));
        return String.format("%s%s", fLetter, decrypt.substring(decrypt.indexOf(" ") + 2));
    }

    public synchronized boolean showRemindsByDate(String userChatId, String date) throws InterruptedException {
        int[] remindsId;
        while ((remindsId = getIdOfAllReminds()).length == 0 ) {
            wait();
        }

        int index = 0, count = 0, n = 1;
        service.sendMessage(userChatId, "Через пару секунд пришлю напоминания на "
                + DateManage.dayAndMonth(date));
        Thread.sleep(4700);
        String messageToSend = SHOW_MESSAGE;

        while (index != remindsId.length) {
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindsId[index]);
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());
            if ((remind.getDetails().getChatIdToSend() == Integer.parseInt(userChatId)&&
                    remind.getRemindDate().equals(date.replaceAll("\\p{P}", "\\.")))
                    && !isContainsDailySendMarker(decrypt)) {
                messageToSend = messageToSend + (n) + ") " + decrypt+"\n";
                count++;
            }
            index++;
        }
                if(count != 0) this.service.sendMessage(userChatId, messageToSend);
                Thread.sleep(5700);
                this.deleteService.deleteMessage(new Message(userChatId, SendMessageServiceImpl.getMessageId()));
                this.service.sendMessage(userChatId, "Was showed");
        return count > 0;
    }

    private String messageForLonelyRemind(Remind remind) {
        String messageToSend = REMIND_MESSAGE;
        String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                getEncryptedMaintenance()), remind.getKey());

        if(isContainsDailySendMarker(decrypt)){
            messageToSend = messageToSend + deleteRegularMarker(remind);
        }
        else{
            messageToSend = (messageToSend +
                String.valueOf(decrypt.
                        charAt(0)).toLowerCase(Locale.ROOT) + decrypt.substring(1));
        }

        updateRemindFieldsToNextSendTime(remind, remind.getDetails().getCountSendOfRemind()+1);
            if(remind.getDetails().getCountSendOfRemind() == 3){
                if(isContainsDailySendMarker(decrypt)){
                    String date = DateManage.nextDate(remind.getRemindDate());
                    updateRemindFieldsToNextDay(remind, date);
                }
                else {
                    RemindServiceImpl.newRemindService().deleteRemind(remind.getId());
                }

            }
        return messageToSend;
    }


    private String messageForAggregateRemind(Remind[] reminds) {
        String messageToSend = REMIND_MESSAGE + "сделать следующее:\n";
        String maintenance;
        for (int i = 0; i < reminds.length; i++) {
            Remind remind = reminds[i];
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());
            int num = (i + 1);
            if(isContainsDailySendMarker(decrypt)){
            maintenance  = String.format("%s%s", Character.
                    toUpperCase(deleteRegularMarker(remind).charAt(0)), deleteRegularMarker(remind).substring(1)) ;
            }
            else {
               maintenance = String.valueOf(decrypt.
                    charAt(0)).toUpperCase(Locale.ROOT)+ decrypt.substring(1);
           }

            messageToSend = messageToSend + num + ") " + maintenance + "." + "\n";

            updateRemindFieldsToNextSendTime(reminds[i],
                    reminds[i].getDetails().getCountSendOfRemind() + 1);
        }


        for (int i = 0; i < reminds.length; i++) {
            Remind remind = reminds[i];

            String decrypted = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());

            if ((isContainsDailySendMarker(decrypted))) {
                if (remind.getDetails().getCountSendOfRemind() == 3) {
                    String date = DateManage.nextDate(reminds[i].getRemindDate());
                    updateRemindFieldsToNextDay(reminds[i], date);
                }
            } else {
                if (remind.getDetails().getCountSendOfRemind() == 3) {
                    RemindServiceImpl.newRemindService().deleteRemind(remind.getId());
                }
            }
        }

        return messageToSend;
    }


    public boolean isChangedRemind(Remind remind, int index) {
        double time = TimeManage.toDoubleTime(TimeManage.currentTime());
        System.out.println(remind.getDetails().isTimeToSend());
        if (!remind.getDetails().isTimeToSend()) {
            if ((TimeManage.timeDifference(remind.getDetails().getLastSendTime()) >= 4.01) && (time < 23)) {
                RemindServiceImpl.newRemindService().updateSendHourField(remind, TimeManage.currentTime());
                RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
                return true;
            }
        }
        if (time >= 23 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)
                || time <= 3 && (remind.getDetails().getCountSendOfRemind() <= 3 &&
                remind.getDetails().getCountSendOfRemind() >= 1)) {

            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());

            if (isContainsDailySendMarker(decrypt)) {
                String date = DateManage.nextDate(remind.getRemindDate());
                updateRemindFieldsToNextDay(remind, date);
            } else {
                RemindServiceImpl.newRemindService().deleteRemind(index);
            }
        }
        return false;
    }

    public void updateRemindFieldsToNextDay(Remind remind, String date) {
        RemindServiceImpl.newRemindService().updateRemindDateField(remind, date);
        RemindServiceImpl.newRemindService().updateCountSendField(remind, 0);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, true);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, "...");
    }

    public void updateRemindFieldsToNextSendTime(Remind remind, int count) {
        RemindServiceImpl.newRemindService().updateCountSendField(remind, count);
        RemindServiceImpl.newRemindService().updateTimeToSendField(remind, false);
        RemindServiceImpl.newRemindService().updateSendHourField(remind, TimeManage.currentTime());
    }

    private void deleteNotUpdatedRemind() {
        List<Remind> reminds = RemindServiceImpl.newRemindService().getAllRemindsFromDB();
        reminds.forEach((r) -> {
            if(DateManage.currentDate().equals(DateManage.nextDate(r.getRemindDate()))){
                RemindServiceImpl.newRemindService().deleteRemind(r.getId());
            }
        });
    }

    private boolean send(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        List<Message> messages = MessageServiceImpl.newMessageService().getAllMessages();

        Remind remind = reminds.get(0);
        String maintenance, chatId = String.valueOf(remind.getDetails().getChatIdToSend());

        if (reminds.size() == 1) {
               maintenance  = messageForLonelyRemind(remind); }
        else   maintenance = messageForAggregateRemind(reminds.toArray(Remind[]::new));

                if(this.service.sendMessage(chatId, maintenance)) {
                    String key = XORCrypt.keyGenerate();
                    String em = XORCrypt.encrypt(maintenance, key);
                    Message newMessage = new Message(chatId, em, key,
                            SendMessageServiceImpl.getMessageId());
                    Message oldMessage;
                    if (!messages.contains(newMessage)){
                        MessageServiceImpl.newMessageService().save(newMessage); }
                    else{ oldMessage = MessageServiceImpl.
                            newMessageService().getMessageByNextField(chatId, maintenance);
                            this.deleteService.deleteMessage(oldMessage);
                    MessageServiceImpl.newMessageService().deleteMessage(oldMessage);
                    MessageServiceImpl.newMessageService().save(newMessage); }
                }
        return true; }

    public boolean isUpdatedToNextDay(Remind remind){
        return remind.getDetails().getLastSendTime().equals("...");
    }


}























