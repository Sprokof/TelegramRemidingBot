package telegramBot.service;


import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.crypt.XORCrypt;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.manage.RemindManage;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RemindServiceImpl implements RemindService {
    private RemindDAOImpl remindDAO;

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
            reminds = (ArrayList<Remind>) session.createSQLQuery("SELECT * from REMINDERS").
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
        List<Object> objects = new ArrayList<>();
        Session session;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            objects = (List<Object>) session.createSQLQuery("SELECT * FROM REMINDERS as r join DETAILS as d " +
                            "on r.details_id = d.id WHERE d.CHAT_ID_TO_SEND=:chatId " +
                            "AND r.REMIND_DATE=:rm").
                    addEntity("r", Remind.class).
                    addJoin("d", "r.details").
                    setParameter("chatId", remind.getDetails().getChatIdToSend()).
                    setParameter("rm", remind.getRemindDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        if(objects.isEmpty()){return false;}

        List<Remind> reminds = new ArrayList<>();
        Iterator<Object> iterator = objects.iterator();
        while(iterator.hasNext()){
            Object[] line = (Object[]) iterator.next();
            Remind thisRemind = (Remind) line[0];
            thisRemind.setDetails((Details) line[1]);
            reminds.add(thisRemind);
        }

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
    public void updateIsStopField(Remind remind, boolean flag) {
        remind.getDetails().setStop(flag);
        this.remindDAO.update(remind);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllExecutingReminds(Remind remind) {
        Session session;
        List<Object> objects = new ArrayList<>();
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            objects = session.createSQLQuery("SELECT * FROM REMINDERS as r join DETAILS as d " +
                                    "on r.details_id = d.id WHERE d.CHAT_ID_TO_SEND =:chatId " +
                                    "AND d.TIME_TO_SEND is true AND " +
                            "d.IS_STOP is false AND r.REMIND_DATE =:currentDate").
                            addEntity("r", Remind.class).
                            addJoin("d", "r.details").
                            setParameter("chatId", remind.getDetails().getChatIdToSend()).
                            setParameter("currentDate", RemindManage.currentDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        if(!objects.isEmpty()){
            List<Remind> reminds = new LinkedList<>();
            for(Iterator<Object> itr = objects.iterator(); itr.hasNext();){
               Object[] line = (Object[]) itr.next();
               Remind r = (Remind) line[0];
               r.setDetails((Details) line[1]);
               reminds.add(r);
            }
            if(RemindManage.toDoubleTime() >= 5.10) { return reminds; }
        return new ArrayList<>();}
        return new ArrayList<>();}

    
}








