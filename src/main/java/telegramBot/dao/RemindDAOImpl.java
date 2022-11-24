package telegramBot.dao;


import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;
import telegramBot.manage.TimeManage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class RemindDAOImpl implements RemindDAO {

    private final SessionFactory sessionFactory = InstanceSessionFactory.getInstance();

    @Override
    public boolean deleteByID(int id) {
        Session session = null;
        Remind remind = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            if ((remind = session.get(Remind.class, id)) != null) {
                session.delete(remind);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return false;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return true;
    }

    @Override
    public boolean save(Remind remind) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save((Remind) remind);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return false;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return true;
    }

    @Override
    public boolean update(Remind remind) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(remind);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return false;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return true;
    }


    @Override
    public Remind getRemindByID(int id) {
        Session session = null;
        Remind remind = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            remind = session.get(Remind.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return remind;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllExecutingReminds(Remind remind) {
        if (TimeManage.toDoubleTime(TimeManage.currentTime()) <= 5.01) return new ArrayList<>();

        Session session = null;
        List<Remind> reminds = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            reminds = session.createSQLQuery("SELECT * FROM USERS_REMINDS as r join REMINDS_DETAILS as d " +
                            "on r.details_id = d.id join USERS as u on r.user_id = u.id" +
                            " WHERE u.CHAT_ID =:chatId " +
                            "AND d.TIME_TO_SEND is true AND " +
                            "u.IS_ACTIVE is true AND r.REMIND_DATE =:currentDate").
                    addEntity("r", Remind.class).
                    setParameter("chatId", remind.getUser().getChatId()).
                    setParameter("currentDate", DateManage.currentDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return reminds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getIdOfAllReminds() {
        Session session = null;
        List<Integer> ides = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            ides = session.createSQLQuery("SELECT ID FROM USERS_REMINDS").list();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ides;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Remind> getAllRemindsFromDB() {
        Session session = null;
        ArrayList<Remind> reminds = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            reminds = (ArrayList<Remind>) session.createSQLQuery("SELECT * FROM USERS_REMINDS").
                    addEntity(Remind.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return reminds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isExistRemind(User user, Remind remind) {
        user.addRemind(remind);
        String decryptMaintenance = XORCrypt.
                decrypt(XORCrypt.stringToIntArray(remind.getEncryptedMaintenance()),
                        remind.getDetails().getKey());
        Session session = null;
        List<Remind> reminds = new LinkedList<>();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            reminds = (LinkedList<Remind>) session.createSQLQuery("SELECT * FROM USERS_REMINDS as r " +
                            "join USERS as u on r.user_id = u.id" +
                            " WHERE u.CHAT_ID=:chatId " +
                            "AND r.REMIND_DATE=:rm").
                    addEntity("r", Remind.class).
                    setParameter("chatId", remind.getUser().getChatId()).
                    setParameter("rm", remind.getRemindDate()).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        if (reminds.isEmpty()) return false;

        return reminds.stream().map((r) -> {
            return XORCrypt.decrypt(XORCrypt.stringToIntArray(r.getEncryptedMaintenance()),
                    r.getDetails().getKey());
        }).anyMatch((m) -> m.equals(decryptMaintenance));
    }

    @SuppressWarnings("unchecked")
    public String getMaxTime(Remind remind) {
        Session session = null;
        List<Remind> reminds = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            reminds = session.createSQLQuery("SELECT * FROM REMINDS as r " +
                            "join USERS as u on r.user_id = u.id " +
                            "WHERE u.CHAT_ID=:id AND r.REMIND_DATE=:rd").
                    addEntity("r", Remind.class)
                    .setParameter("id", remind.getUser().getChatId())
                    .setParameter("rd", remind.getRemindDate()).list();
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        if (!reminds.isEmpty()) {
            List<Double> times =
                    reminds.stream().map((r) -> {
                        return TimeManage.toDoubleTime(r.getDetails().getLastSendTime());
                    }).sorted((d1, d2) -> d2.compareTo(d1)).collect(Collectors.toList());

            String time = TimeManage.toStringTime(times.get(0));
            if (time.length() == 4) return time + "0";
            return time;
        }
        return null;
    }

    @Override
    public void insertIntoExecutedRemind(Remind remind) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            session.createSQLQuery("INSERT INTO EXECUTED_REMINDS " +
                            "(remind_id, executed_date) VALUES(:remindId, :date)").
                    setParameter("remindId", remind.getId()).
                    setParameter("date", remind.getRemindDate()).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean remindExecuted(Remind remind) {
        Session session = null;
        Integer id = null;
        try {
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            id = (Integer) session.createSQLQuery("SELECT REMIND_ID FROM " +
                            "EXECUTED_REMINDS WHERE remind_id=:id").
                    setParameter("id", remind.getId()).getSingleResult();
            session.getTransaction().rollback();
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) session.close();
        }
        return (id != null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Remind findExecutedRemindByDate(String date) {
        Session session = null;
        List<Integer> ides = null;
        try {
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            ides = (List<Integer>) session.createSQLQuery("SELECT ID FROM EXECUTED_REMINDS " +
                            "WHERE EXECUTED_DATE=:date").
                    setParameter("date", date).list();
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) session.close();
        }
        if (ides != null && !ides.isEmpty()) {
            int lastIndex = (ides.size() - 1);
            return getRemindByID(ides.get(lastIndex));
        }
        return null;
    }

    @Override
    public void deleteExecutedRemindsById(int remindId) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            session.createSQLQuery("DELETE FROM " +
                            "EXECUTED_REMINDS WHERE REMIND_ID=:id")
                    .setParameter("id", remindId).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}






