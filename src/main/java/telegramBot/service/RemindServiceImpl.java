package telegramBot.service;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Remind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemindServiceImpl implements RemindService{
    private RemindDAOImpl remindDAO;

    @Autowired
    public RemindServiceImpl(RemindDAOImpl remindDAO){
        this.remindDAO = remindDAO;
    }

    @Override
    public boolean saveRemind(Remind remind) {
        return this.remindDAO.save(remind);
    }

    @Override
    public synchronized void deleteRemind(int[] arrayId, int index, int[] newArrayId) throws InterruptedException{
        try {
            this.remindDAO.deleteByID(arrayId[index]);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println("delete sent notice");
            wait(2000);
            notify();
            arrayId = newArrayId;
        }
    }

    @Override
    public void updateDate(Remind remind, String newDate) {
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
            temp = session.createSQLQuery("SELECT id," +
                    "MAINTENANCE, REMIND_DATE, USER_CHAT_ID from REMINDERS").
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
            if(rem.equals(remind)){return true;}}
        return false;}
        }




