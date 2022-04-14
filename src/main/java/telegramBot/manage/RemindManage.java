package telegramBot.manage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.service.*;

import java.util.*;
import java.util.stream.Collectors;

import static telegramBot.service.RemindServiceImpl.*;

@Component
public class RemindManage {

    private final SendMessageServiceImpl service;
    private final DeleteMessageServiceImpl deleteService;

    @Getter
    private static final String REMIND_MESSAGE = "(R) Позвольте напомнить, что вам следует ";
    private static final String SHOW_MESSAGE = "(R) На эту дату есть следующие записи:\n";
    private static final int countSend = 4;
    private static final double hour = 3.01;

    @Autowired
    public RemindManage(SendMessageService service, DeleteMessageServiceImpl deleteService) {
        this.service = (SendMessageServiceImpl) service;
        this.deleteService = deleteService;
    }


    public synchronized void execute() throws InterruptedException {
        List<Integer> remindsId;
        while ((remindsId = newRemindService().getIdOfAllReminds()).isEmpty()){
            wait();
        }
        List<Remind> reminds = new ArrayList<>();
        for (int index = 0; index < remindsId.size(); index++) {
            Remind remind = RemindServiceImpl.newRemindService().getRemindById(remindsId.get(index));
            if ((remind.getRemindDate().equals(DateManage.currentDate())
                    && (!remind.getUser().isActive()))) {
                if(isChangedRemind(remind, remindsId.get(index)) || isUpdatedToNextDay(remind)) {
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
                messages.forEach(this.deleteService::deleteMessage);
            }
            MessageServiceImpl.newMessageService().deleteAllMessages();
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
        List<Integer> remindsId;
        while ((remindsId = newRemindService().getIdOfAllReminds()).size() == 0 ) {
            wait();
        }

        int index = 0, count = 0, n = 1;
        service.sendMessage(userChatId, "Через пару секунд пришлю напоминания на "
                + DateManage.dayAndMonth(date));
        Thread.sleep(4700);
        String messageToSend = SHOW_MESSAGE;

        while (index != remindsId.size()) {
            Remind remind = newRemindService().getRemindById(remindsId.get(index));
            String decrypt = XORCrypt.decrypt(XORCrypt.stringToIntArray(remind.
                    getEncryptedMaintenance()), remind.getKey());
            if ((remind.getUser().getChatId().equals(userChatId) &&
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
                this.service.sendMessage(userChatId, "reminds was showed");
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
            if(remind.getDetails().getCountSendOfRemind() == countSend){
                if(isContainsDailySendMarker(decrypt)){
                    String date = DateManage.nextDate(remind.getRemindDate());
                    updateRemindFieldsToNextDay(remind, date);
                }
                else {
                    User user = remind.getUser();
                    user.removeRemind(remind);
                    newRemindService().deleteRemind(remind.getId());
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
                if (remind.getDetails().getCountSendOfRemind() == countSend) {
                    String date = DateManage.nextDate(reminds[i].getRemindDate());
                    updateRemindFieldsToNextDay(reminds[i], date);
                }
            } else {
                if (remind.getDetails().getCountSendOfRemind() == countSend) {
                    User user = remind.getUser();
                    user.removeRemind(remind);
                    newRemindService().deleteRemind(remind.getId());
                }
            }
        }

        return messageToSend;
    }


    public boolean isChangedRemind(Remind remind, int index) {
        double time = TimeManage.toDoubleTime(TimeManage.currentTime());
        if (!remind.getDetails().isTimeToSend()) {
            if ((TimeManage.timeDifference(remind.getDetails().getLastSendTime()) >= hour) && (time < 23)) {
                newRemindService().updateSendHourField(remind, TimeManage.currentTime());
                newRemindService().updateTimeToSendField(remind, true);
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
                User user = remind.getUser();
                user.removeRemind(remind);
                newRemindService().deleteRemind(index);
            }
        }
        return false;
    }

    public void updateRemindFieldsToNextDay(Remind remind, String date) {
        newRemindService().updateRemindDateField(remind, date);
        newRemindService().updateCountSendField(remind, 0);
        newRemindService().updateTimeToSendField(remind, true);
        newRemindService().updateSendHourField(remind, "...");
    }

    public void updateRemindFieldsToNextSendTime(Remind remind, int count) {
        newRemindService().updateCountSendField(remind, count);
        newRemindService().updateTimeToSendField(remind, false);
        newRemindService().updateSendHourField(remind, TimeManage.currentTime());
    }

    private void deleteNotUpdatedRemind() {
        List<Remind> reminds = newRemindService().getAllRemindsFromDB();
        reminds.forEach((r) -> {
            if(DateManage.nextDate(r.getRemindDate()).equals(DateManage.currentDate())){
                User user = r.getUser();
                user.removeRemind(r);
                newRemindService().deleteRemind(r.getId());
            }
        });
    }

    private boolean send(final List<Remind> reminds) {
        if (reminds.isEmpty()) return false;

        List<Message> messages = MessageServiceImpl.newMessageService().getAllMessages();

        Remind remind = reminds.get(0);
        String maintenance, chatId = String.valueOf(remind.getUser().getChatId());
        String id = getIdOfReminds(reminds);

        if (reminds.size() == 1) {
               maintenance = messageForLonelyRemind(remind); }
        else   maintenance = messageForAggregateRemind(reminds.toArray(Remind[]::new));

                if(this.service.sendMessage(chatId, maintenance)) {
                    Message newMessage = new Message(chatId, sort(id),
                            SendMessageServiceImpl.getMessageId());
                    Message oldMessage;
                    if (!messages.contains(newMessage)){
                        MessageServiceImpl.newMessageService().save(newMessage); }
                    else{ oldMessage = MessageServiceImpl.
                            newMessageService().getMessageByNextField(chatId, sort(id));
                            this.deleteService.deleteMessage(oldMessage);
                    MessageServiceImpl.newMessageService().deleteMessage(oldMessage);
                    MessageServiceImpl.newMessageService().save(newMessage); }
                }
        return true; }



    public boolean isUpdatedToNextDay(Remind remind){
        return remind.getDetails().getLastSendTime().equals("...");
    }

    private String getIdOfReminds(List<Remind> reminds){
    if(reminds.size() == 1) return String.valueOf(reminds.get(0).getId());
    StringBuffer sb = new StringBuffer("");
    for(int i = 0; i < reminds.size(); i ++){
        Remind r = reminds.get(i);
            sb.append(r.getId()); }
    return sb.toString();
    }

    private String sort(String id){
        if(id.length() == 1) return id;
        List<Integer> ints;
        ints = Arrays.stream(id.split("")).
                map(Integer::parseInt).collect(Collectors.toList());
        ints.remove(0);
        ints.sort((i1, i2)-> i1-i2);
        return Arrays.toString(ints.toArray(Integer[]::new)).toString().
                replaceAll("\\p{P}", "");
    }


}























