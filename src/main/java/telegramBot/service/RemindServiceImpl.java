package telegramBot.service;


import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.crypt.XORCrypt;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.manage.DateManage;
import telegramBot.manage.TimeManage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemindServiceImpl implements RemindService {

    private final RemindDAOImpl remindDAO;

    @Autowired
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
    public void updateRemindDateField(Remind remind, String newDate) {
        remind.setRemindDate(newDate);
        this.remindDAO.update(remind);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllRemindsFromDB() {
        Session session;
        ArrayList<Remind> reminds = null;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            reminds = (ArrayList<Remind>) session.createSQLQuery("SELECT * from REMINDS").
                    addEntity(Remind.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        return reminds;
    }

    @Override
    public Remind getRemindById(int id) {
        return this.remindDAO.getObjectByID(id);
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean isExist(Remind remind) {
        String decryptMaintenance = XORCrypt.
                decrypt(XORCrypt.stringToIntArray(remind.getEncryptedMaintenance()), remind.getKey());
        List<Remind> reminds = new ArrayList<>();
        Session session;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            reminds = (List<Remind>) session.createSQLQuery("SELECT * FROM REMINDS as r " +
                            "join USERS as u on r.user_id = u.id" +
                            " WHERE u.CHAT_ID=:chatId " +
                            "AND r.REMIND_DATE=:rm").
                    addEntity("r", Remind.class).
                    setParameter("chatId", remind.getUser().getChatId()).
                    setParameter("rm", remind.getRemindDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        if(reminds.isEmpty()){return false;}

        return reminds.stream().map((r)->{
            return XORCrypt.decrypt(XORCrypt.stringToIntArray(r.getEncryptedMaintenance()),
                    r.getKey());}).anyMatch((m)-> m.equals(decryptMaintenance));
    }


    public static RemindServiceImpl newRemindService() {
        return new RemindServiceImpl(new RemindDAOImpl());
    }


    @Override
    public void updateTimeToSendField(Remind remind, boolean flag) {
        remind.getDetails().setTimeToSend(flag);
        this.remindDAO.update(remind);

    }

    @Override
    public void updateCountSendField(Remind remind, int count) {
        remind.getDetails().setCountSendOfRemind(count);
        this.remindDAO.update(remind);
    }

    @Override
    public void updateSendHourField(Remind remind, String time) {
        remind.getDetails().setLastSendTime(time);
        this.remindDAO.update(remind);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllExecutingReminds(Remind remind) {
        if(TimeManage.toDoubleTime(TimeManage.currentTime()) <= 5.09) return new ArrayList<>();
        Session session;
        List<Remind> reminds = new ArrayList<>();
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            reminds = session.createSQLQuery("SELECT * FROM REMINDS as r join DETAILS as d " +
                                    "on r.details_id = d.id join USERS as u on r.user_id = u.id" +
                            " WHERE u.CHAT_ID=:chatId " +
                                    "AND d.TIME_TO_SEND is true AND " +
                            "u.IS_ACTIVE is true AND r.REMIND_DATE =:currentDate").
                            addEntity("r", Remind.class).
                            setParameter("chatId", remind.getUser().getChatId()).
                            setParameter("currentDate", DateManage.currentDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        return reminds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getMaxTime(Remind remind) {
        Session session;
        List<Remind> reminds = new ArrayList<>();
    try{
        session = this.remindDAO.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        reminds = session.createSQLQuery("SELECT * FROM REMINDS as r " +
                "join USERS as u on r.user_id = u.id " +
                "WHERE u.CHAT_ID=:id AND r.REMIND_DATE=:rd").addEntity("r", Remind.class)
                .setParameter("id", remind.getUser().getChatId())
                .setParameter("rd", remind.getRemindDate()).list();
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
            this.remindDAO.getSessionFactory().close();
        }

    if(reminds.isEmpty()) return null;

        List<Double> times =
                reminds.stream().map((r) -> {
                    return TimeManage.toDoubleTime(r.getDetails().getLastSendTime());
                }).sorted((d1, d2) -> d2.compareTo(d1)).collect(Collectors.toList());

        String time = TimeManage.toStringTime(times.get(0));
        if(time.length() == 4) return time+"0";
        return time;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getIdOfAllReminds() {
        Session session;
        List<Integer> ides = new ArrayList<>();
    try{
        session = this.remindDAO.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        ides = session.createSQLQuery("SELECT ID FROM REMINDS").list();
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.remindDAO.getSessionFactory().close();
    }
    if(ides.isEmpty()) return new ArrayList<>();
    return ides;
    }
}








