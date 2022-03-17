package telegramBot.service;

import org.hibernate.QueryException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        return this.remindDAO.getObjectByID(id);}

    @Override
    public boolean isExist(Remind remind) throws NullPointerException {
        Session session;
        Remind[] reminds = null;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            try {
                reminds = (Remind[]) session.createSQLQuery("SELECT * FROM REMINDERS " +
                                "WHERE ENCRYPT_MAINTENANCE = " +
                                "?::String" + remind.getEncryptedMaintenance() +
                                "AND REMIND_DATE = ?::String" + remind.getRemindDate()).
                        addEntity(Remind.class).list().toArray();
                session.getTransaction().commit();
            } catch (QueryException e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }
        return Arrays.stream(reminds).map(Remind::getDetails).noneMatch((d)->{
           return d.getChatIdToSend().equals(remind.getDetails().getChatIdToSend()); });
    }


    public static RemindServiceImpl newRemindService(){
        return new RemindServiceImpl(new RemindDAOImpl());
    }

    @Override
    public void updateRemind(Remind remind) {
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
    public void updateSendHourField(Remind remind, String time) {
        remind.getDetails().setLastSendTime(time);
        this.remindDAO.update(remind);
    }

    @Override
    public void updateIsStopField(Remind remind, boolean flag) {
        remind.getDetails().setIsStop(String.valueOf(flag));
        this.remindDAO.update(remind);
    }

    @Override
    public List<Remind> getAllExecutingRemindsByChatId(Integer chatId) {
        List<Remind> reminds =
                (ArrayList<Remind>) getAllRemindsFromDB().stream().filter((r) -> {
                            return Objects.equals(r.getDetails().getChatIdToSend(), chatId) &&
                                    r.getRemindDate().replaceAll("\\p{P}", "\\.").equals(SendRemind.currentDate()) &&
                                    r.getDetails().getTimeToSend().equals("true") && r.getDetails().getIsStop().equals("false");
                        }).
                        collect(Collectors.toList());
        double time = SendRemind.toDoubleTime();
        if (time >= 5.00) return reminds;
        return new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllNotExecutingRemindsByChatId(Integer chatId) {
        List<Details> details = null;
        List<Integer> ides;
        List<Remind> reminds = new ArrayList<>();

        Session session;
        try {
            session = this.remindDAO.getSessionFactory().getCurrentSession();
            session.beginTransaction();
        try{
            details = (List<Details>) session.createSQLQuery("SELECT * FROM DETAILS WHERE CHAT_ID_TO_SEND = " + chatId +
                    "AND TIME_TO_SEND = ?::String false").addEntity(Details.class).list();
            session.getTransaction().commit();}
        catch (QueryException e){ return new ArrayList<>();}

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.remindDAO.getSessionFactory().close();
        }

        assert details != null;
        ides = details.stream().map(Details::getId).collect(Collectors.toList());

        int index = 0;
        while (index != ides.size()) {
            Remind remind;
            int id = ides.get(index);
            if (!(remind = getRemindById(id)).
                    getRemindDate().equals(SendRemind.currentDate())) {
                continue;
            }
            reminds.add(remind);
        }
        return reminds;
    }
}








