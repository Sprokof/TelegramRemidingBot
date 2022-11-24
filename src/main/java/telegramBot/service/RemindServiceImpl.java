package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;
import telegramBot.manage.TimeManage;

import java.util.List;

@Component
public class RemindServiceImpl implements RemindService {

    @Autowired
    private SendMessageService messageService;

    private final RemindDAOImpl remindDAO;


    public RemindServiceImpl(RemindDAOImpl remindDAO) {
        this.remindDAO = remindDAO;
    }

    @Override
    public boolean saveRemind(Remind remind) {
        return this.remindDAO.save(remind);
    }

    @Override
    public void deleteRemind(int id) {
        this.remindDAO.deleteByID(id);
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
    public void updateSendHourField(Remind remind, String time) {
        remind.getDetails().setLastSendTime(time);
        this.remindDAO.update(remind);
        deleteExecutedRemind(remind);
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
        Remind executedRemind;
        String time;
        if ((executedRemind = findExecutedRemindByDate(remind.getRemindDate())) != null) {
            time = executedRemind.getDetails().getLastSendTime();
        }
        else {
            if(remind.getRemindDate().equals(DateManage.currentDate())) {
                time = TimeManage.currentTime();
            }
            else time = TimeManage.DEFAULT_TIME;
        }
        remind.getDetails().setLastSendTime(time);
    }

    @Override
    public void markAsExecuted(Remind remind) {
        if(remindExecuted(remind)) return ;
        this.remindDAO.insertIntoExecutedRemind(remind);
    }

    @Override
    public boolean remindExecuted(Remind remind) {
        return this.remindDAO.remindExecuted(remind);
    }

    @Override
    public Remind findExecutedRemindByDate(String date) {
        return this.remindDAO.findExecutedRemindByDate(date);
    }

    @Override
    public boolean sendRemind(String chatId, String maintenance) {
        return this.messageService.sendMessage(chatId, maintenance);
    }

    @Override
    public void deleteExecutedRemind(Remind remind) {
        this.remindDAO.deleteExecutedRemindsById(remind.getId());
    }
}







