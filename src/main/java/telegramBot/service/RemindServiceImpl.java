package telegramBot.service;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RemindServiceImpl implements RemindService{
    private RemindDAOImpl remindDAO;

    @Autowired
    public RemindServiceImpl(RemindDAOImpl remindDAO){
        this.remindDAO = remindDAO;
    }

    @Override
    public boolean saveRemind(Remind remind, Details details) {
        return this.remindDAO.save(remind, details);
    }

    @Override
    public void deleteRemind(int index) {
        this.remindDAO.deleteByID(index);
    }

    @Override
    public void updateRemindDateField(Remind remind, String newDate) {
        remind.setRemindDate(newDate);
        this.remindDAO.update(remind);
    }

    @Override
    public List<Remind> getAllRemindsFromDB() {
        Session session;
        List<?> temp = null;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            temp = session.createSQLQuery("SELECT * from REMINDERS").
                    addEntity(Remind.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        List<Remind> reminds = new ArrayList<>();
        for (Iterator<?> it = temp.iterator(); it.hasNext();) {
            reminds.add((Remind) it.next());
        }
        return reminds;
    }

    @Override
    public Remind getRemindById(int id) {
        return this.remindDAO.getObjectByID(id);}

    @Override
    public boolean isContainsInDB(Remind remind) {
        List<Remind> reminds = getAllRemindsFromDB();
        for(Remind rem:reminds){
            if(rem.equals(remind)){return true;}
        }
        return false;}

    public static RemindServiceImpl newRemindService(){
        return new RemindServiceImpl(new RemindDAOImpl());
    }

    @Override
    public void updateMaintenanceField(Remind remind, String maintenance) {
        remind.setEncryptedMaintenance(maintenance);
        this.remindDAO.update(remind);
    }

    @Override
    public void updateTimeToSendField(Remind remind, boolean flag) {
        remind.getDetails().setTimeToSend(String.valueOf(flag));
        this.remindDAO.update(remind);

    }

    @Override
    public void updateCountSendField(Remind remind, int count) {
        remind.getDetails().setCountSendOfRemind(count);
        this.remindDAO.update(remind);
    }

    @Override
    public void updateSendHourField(Remind remind, int hour) {
        remind.getDetails().setLastSendHour(hour);
        this.remindDAO.update(remind);
    }

    @Override
    public void updateIsStopField(Remind remind, boolean flag) {
        remind.getDetails().setIsStop(String.valueOf(flag));
        this.remindDAO.update(remind);
    }

    @Override
    public List<Remind> getAllExecutingRemindsByChatId(String chatId) {
        List<Remind> reminds =
                getAllRemindsFromDB().stream().filter((r) -> {
                            return r.getDetails().getChatIdToSend().equals(chatId) &&
                                    r.getRemindDate().replaceAll("\\p{P}", "\\.").equals(SendRemind.currentDate()) &&
                                    r.getDetails().getTimeToSend().equals("true") && r.getDetails().getIsStop().equals("false");
                        }).
                        collect(Collectors.toList());
        if (SendRemind.currentTime() >= 5) return reminds;
        return new ArrayList<>();
    }


    }








