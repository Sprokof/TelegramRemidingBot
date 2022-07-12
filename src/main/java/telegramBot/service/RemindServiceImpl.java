package telegramBot.service;

import org.springframework.stereotype.Component;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;
import telegramBot.manage.TimeManage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemindServiceImpl implements RemindService {

    private final RemindDAOImpl remindDAO;

    public RemindServiceImpl(RemindDAOImpl remindDAO) {
        this.remindDAO = remindDAO;
    }

    @Override
    public boolean saveRemind(Remind remind) {

        return this.remindDAO.save(remind);
    }

    @Override
    public void deleteRemind(int index) {
        this.remindDAO.deleteByID(index);
    }

    @Override
    public RemindServiceImpl updateRemindDateField(Remind remind, String newDate) {
        remind.setRemindDate(newDate);
        this.remindDAO.update(remind);
        return this;
    }

    @Override
    public List<Remind> getAllRemindsFromDB() {
        return this.remindDAO.getAllRemindsFromDB();
    }

    @Override
    public Remind getRemindById(int id) {
        return this.remindDAO.getRemindByID(id);
    }

    @Override
    public RemindServiceImpl updateTimeToSendField(Remind remind, boolean flag) {
        remind.getDetails().setTimeToSend(flag);
        this.remindDAO.update(remind);
        return this;

    }

    @Override
    public RemindServiceImpl updateCountSendField(Remind remind, int count) {
        remind.getDetails().setCountSendOfRemind(count);
        this.remindDAO.update(remind);
        return this;
    }

    @Override
    public RemindServiceImpl updateSendHourField(Remind remind, String time) {
        remind.getDetails().setLastSendTime(time);
        this.remindDAO.update(remind);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllExecutingReminds(Remind remind) {
        return this.remindDAO.getAllExecutingReminds(remind);
    }

    @Override
    public boolean isExistRemind(User user, Remind remind){
        return this.remindDAO.isExistRemind(user, remind);
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getIdOfAllReminds() {
        return this.remindDAO.getIdOfAllReminds();
    }

    @Override
    public void extendsLastSendTimeIfAbsent(Remind remind) {
        String chatId = remind.getUser().getChatId();
        List<Message> remindMessage =
                MessageServiceImpl.messageService().getRemindMessagesByChatId(chatId);
        Message message;
        int lastIndex = remindMessage.size() - 1;
        String time = this.remindDAO.getMaxTime(remind);
        if (time != null) {
            remind.getDetails().setLastSendTime(time);
                if ((!remindMessage.isEmpty()) && (message = remindMessage.get(lastIndex)) != null) {
                    int remindId = getLastId(chatId) + 1;
                    message.setRemindId((String.format("%s%s%d",
                            message.getRemindId(), "/", remindId)));
                    MessageServiceImpl.
                            messageService().updateMessage(message);
            }
        }
        else {
            if(DateManage.currentDate().
                    equals(DateManage.currentDate())){
                double temp = TimeManage.toDoubleTime(TimeManage.currentTime()) - 2;
                if ((time = TimeManage.
                        toStringTime(temp)).length() == 1) {
                    time += "0";
                }
            remind.getDetails().setLastSendTime(time);
            }
        }

    }

    private int getLastId(String chatId) {
        return this.remindDAO.getAllRemindsFromDB().
                stream().filter((r) -> {
                    return r.getUser().getChatId().equals(chatId);
                }).sorted((r1, r2) -> r2.getId() - r1.getId()).
                collect(Collectors.toList()).get(0).getId();
    }

}







